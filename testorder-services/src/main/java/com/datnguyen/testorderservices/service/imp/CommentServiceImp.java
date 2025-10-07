package com.datnguyen.testorderservices.service.imp;

import com.datnguyen.testorderservices.client.PatientClient;
import com.datnguyen.testorderservices.client.PatientDTO;
import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.dto.request.CommentUpdateRequest;
import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.dto.response.CommentUpdateResponse;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.entity.AuditLogComment;
import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.entity.TestResult;
import com.datnguyen.testorderservices.repository.AuditLogCommentRepository;
import com.datnguyen.testorderservices.repository.CommentRepository;
import com.datnguyen.testorderservices.repository.TestOrderRepository;
import com.datnguyen.testorderservices.repository.TestResultRepository;
import com.datnguyen.testorderservices.service.CommentService;
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
                log.warn("⚠️ Patient with id={} not found", commentRequest.getUserId());
                throw new RuntimeException("Patient not found");
            }

            // ✅ Kiểm tra nội dung comment
            if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
                log.warn("⚠️ Comment content is empty for userId={}", commentRequest.getUserId());
                throw new IllegalArgumentException("Comment content cannot be empty.");
            }

            Comment comment = new Comment();
            comment.setUserId(commentRequest.getUserId());
            comment.setContent(commentRequest.getContent());

            // ✅ Gắn với TestOrder hoặc TestResult
            if (commentRequest.getTestOrderId() != null) {
                log.debug("🔍 Fetching TestOrder with id={}", commentRequest.getTestOrderId());
                TestOrder order = testOrderRepository.findById(commentRequest.getTestOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Test Order not found"));
                comment.setTestOrder(order);
            } else if (commentRequest.getTestResultId() != null) {
                log.debug("🔍 Fetching TestResult with id={}", commentRequest.getTestResultId());
                TestResult result = testResultRepository.findById(commentRequest.getTestResultId())
                        .orElseThrow(() -> new IllegalArgumentException("Test Result not found"));
                comment.setTestResult(result);
            } else {
                log.warn("⚠️ Missing testOrderId/testResultId for comment from userId={}", commentRequest.getUserId());
                throw new IllegalArgumentException("Comment must be attached to either a Test Order or a Test Result.");
            }

            // ✅ Lưu vào DB
            Comment saved = commentRepository.save(comment);
            log.info("✅ Comment saved successfully with id={}", saved.getId());

            return saved;

        } catch (Exception e) {
            log.error("❌ Error while adding comment: {}", e.getMessage(), e);
            throw e; // ném lại để GlobalExceptionHandler hoặc Controller xử lý
        }
    }


    @Override
    public List<CommentResponse> getCommentByUserId(Long userId) {

        log.info("🔍 Đang truy vấn comment của userId = {}", userId);

        List<Comment> comments = commentRepository.findByUserId(userId);
        if (comments.isEmpty()) {
            log.warn("⚠️ Không tìm thấy bình luận nào của user có id: {}", userId);
            throw new RuntimeException("Không tìm thấy bình luận nào của user có id: " + userId);
        }

        log.info("✅ Tìm thấy {} bình luận cho userId = {}", comments.size(), userId);

        return comments.stream()
                .peek(c -> log.debug("➡️ Đang convert commentId = {}", c.getId()))
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public CommentUpdateResponse updateComment(CommentUpdateRequest request) {
        //Kiểm tra tồn tại
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + request.getId()));

        if(request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty.");
        }
        //Luu nội dung cũ
        try {
        String oldContent = comment.getContent();
            RestResponse<PatientDTO> patientDTO = patientClient.getById(comment.getUserId());
        if (patientDTO == null) {
            log.warn("⚠️ Patient with id={} not found", comment.getUserId());
            throw new RuntimeException("Patient not found");
        }
        log.info("pt name-------------->" +patientDTO.getData().getFullName());

        //Cập nhật nội dung mới
        System.out.println(comment.getUserId());
        comment.setContent(request.getContent());
        comment.setUpdatedBy(patientDTO.getData().getFullName());
        comment.setUpdatedAt(LocalDateTime.now());


        //ghi vào auditLog
        AuditLogComment auditLogComment = AuditLogComment.builder()
                .action("UPDATE_COMMENT")
                .commentId(comment.getId())
                .updatedBy(patientDTO.getData().getFullName() )
                .oldContent(oldContent)
                .newContent(request.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLogComment);
        commentRepository.save(comment);

        return CommentUpdateResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .updatedBy(patientDTO.getData().getFullName() )
                .updatedAt(comment.getUpdatedAt())
                .build();
        } catch (Exception e) {
            log.error("❌ Error while adding comment: {}", e.getMessage(), e);
            throw e; // ném lại để GlobalExceptionHandler hoặc Controller xử lý
        }

    }

    private CommentResponse convertToDto(Comment comment) {
        log.debug("🛠️ Bắt đầu convert commentId = {}", comment.getId());

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setCommentId(comment.getId());

        try {
            RestResponse<PatientDTO> patientDTO = patientClient.getById(comment.getUserId());
            String userName = patientClient.getById(patientDTO.getData().getId()).getData().getFullName();
            commentResponse.setUserName(userName);
            log.debug("👤 Lấy thông tin userName = {} cho userId = {}", userName, comment.getUserId());
        } catch (Exception e) {
            log.error("❌ Lỗi khi gọi patientClient.getById({}): {}", comment.getUserId(), e.getMessage());
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
