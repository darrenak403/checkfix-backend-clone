package com.datnguyen.testorderservices.service.imp;

import com.datnguyen.testorderservices.client.PatientClient;
import com.datnguyen.testorderservices.client.PatientDTO;
import com.datnguyen.testorderservices.dto.request.CommentDeleteRequest;
import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.dto.request.CommentUpdateRequest;
import com.datnguyen.testorderservices.dto.response.CommentDeleteResponse;
import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.dto.response.CommentUpdateResponse;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.entity.*;
import com.datnguyen.testorderservices.exception.DeleteException;
import com.datnguyen.testorderservices.repository.*;
import com.datnguyen.testorderservices.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private AuditLogCommentRepository auditLogRepository;

    @Autowired
    private AuditDeleteCommentRepository auditDeleteCommentRepository;

    @Override
    public Comment addComment(CommentRequest commentRequest) {
        log.info("📩 Received request to add comment: userId={}, testOrderId={}, testResultId={}",
                commentRequest.getUserId(),
                commentRequest.getTestOrderId(),
                commentRequest.getTestResultId());

        try {
            // ✅ Kiểm tra user tồn tại bên patient-service
            RestResponse<PatientDTO> patientDTO = patientClient.getById(commentRequest.getUserId());
            if (patientDTO == null) {

                throw new RuntimeException("Patient not found");
            }

            // ✅ Kiểm tra nội dung comment
            if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
                log.warn("⚠️ Comment content is empty for userId={}", commentRequest.getUserId());
                throw new IllegalArgumentException("Comment content cannot be empty.");
            }

            Comment comment = new Comment();
            comment.setDoctorId(patientDTO.getData().getCreatedBy());
            comment.setContent(commentRequest.getContent());
            comment.setStatus("ACTIVE");

            // ✅ Gắn với TestOrder hoặc TestResult
            if (commentRequest.getTestOrderId() != null) {

                TestOrder order = testOrderRepository.findById(commentRequest.getTestOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Test Order not found"));
                comment.setTestOrder(order);
            } else if (commentRequest.getTestResultId() != null) {

                TestResult result = testResultRepository.findById(commentRequest.getTestResultId())
                        .orElseThrow(() -> new IllegalArgumentException("Test Result not found"));
                comment.setTestResult(result);
            } else {

                throw new IllegalArgumentException("Comment must be attached to either a Test Order or a Test Result.");
            }

            Comment saved = commentRepository.save(comment);
            return saved;

        } catch (Exception e) {

            throw e; // ném lại để GlobalExceptionHandler hoặc Controller xử lý
        }
    }


    @Override
    public List<CommentResponse> getCommentByDoctorId(Long doctorId) {



        List<Comment> comments = commentRepository.findByDoctorId(doctorId);
        if (comments.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bình luận nào của Doctor có id: " + doctorId);
        }
        return comments.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentUpdateResponse updateComment(CommentUpdateRequest request) {
        //Kiểm tra tồn tại
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + request.getId()));

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty.");
        }
        //Luu nội dung cũ
        try {
            String oldContent = comment.getContent();
            RestResponse<PatientDTO> patientDTO = patientClient.getById(comment.getDoctorId());
            if (patientDTO == null) {
                throw new RuntimeException("Patient not found");
            }

            //ghi vào auditLog
            AuditLogComment auditLogComment = AuditLogComment.builder()
                    .action("UPDATE_COMMENT")
                    .commentId(comment.getId())
                    .updatedBy(patientDTO.getData().getFullName())
                    .oldContent(oldContent)
                    .newContent(request.getContent())
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLogComment);

            //Cập nhật nội dung mới

            comment.setContent(request.getContent());
            comment.setUpdatedBy(patientDTO.getData().getFullName());
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);





            return CommentUpdateResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .updatedBy(patientDTO.getData().getFullName())
                    .updatedAt(comment.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("❌ Error while adding comment: {}", e.getMessage(), e);
            throw e; // ném lại để GlobalExceptionHandler hoặc Controller xử lý
        }

    }

    @Transactional
    @Override
    public CommentDeleteResponse deleteComment(CommentDeleteRequest commentDeleteRequest) {

        //1.Tìm comment
        Comment comment = commentRepository.findById(commentDeleteRequest.getCommentId())
                .orElseThrow(() -> new DeleteException("Comment not found with id: " + commentDeleteRequest.getCommentId()));

        //2.Kiểm tra status comment đã xóa chưa
        if ("DELETED".equals(comment.getStatus())) {
            throw new DeleteException("Comment with id " + comment.getId() + " is already deleted.");
        }

        comment.setStatus("DELETED");
        commentRepository.save(comment);

        //3.Ghi vào auditLog
        RestResponse<PatientDTO> patientDTO = patientClient.getById(commentDeleteRequest.getDeleteById());
        if (patientDTO == null) {
            log.warn("⚠️ Patient with id={} not found", commentDeleteRequest.getDeleteById());
            throw new DeleteException("Patient not found");
        }
        AuditDeleteComment auditDeleteComment = new AuditDeleteComment();
        auditDeleteComment.setAction("DELETE_COMMENT");
        auditDeleteComment.setReferenceId(comment.getId());
        auditDeleteComment.setEntityType("Comment");
        auditDeleteComment.setPerformedBy(patientDTO.getData().getFullName());
        auditDeleteComment.setReason(commentDeleteRequest.getReason());
        auditDeleteComment.setPerformedAt(LocalDateTime.now());

        auditDeleteCommentRepository.save(auditDeleteComment);

        return CommentDeleteResponse.builder()
                .referenceId(comment.getId())
                .action(comment.getStatus())
                .entityType("Comment")
                .performedBy(patientDTO.getData().getFullName())
                .reason(commentDeleteRequest.getReason())
                .performedAt(LocalDateTime.now())
                .build();

    }


    private CommentResponse convertToDto(Comment comment) {
        log.debug("🛠️ Bắt đầu convert commentId = {}", comment.getId());

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setCommentId(comment.getId());

        try {
            RestResponse<PatientDTO> patientDTO = patientClient.getById(comment.getDoctorId());
            String userName = patientClient.getById(patientDTO.getData().getId()).getData().getFullName();
            commentResponse.setUserName(userName);
        } catch (Exception e) {
            commentResponse.setUserName("Không xác định");
        }

        commentResponse.setCommentContent(comment.getContent());
        commentResponse.setTestOrderId(comment.getTestOrder() != null ? comment.getTestOrder().getId() : 0L);
        commentResponse.setTestResultId(comment.getTestResult() != null ? comment.getTestResult().getId() : 0L);
        commentResponse.setCreatedAt(comment.getCreatedAt());

        log.debug("✅ Hoàn tất convert commentId = {}", comment.getId());
        return commentResponse;
    }
}
