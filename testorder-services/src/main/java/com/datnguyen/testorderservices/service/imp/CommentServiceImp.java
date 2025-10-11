package com.datnguyen.testorderservices.service.imp;

import com.datnguyen.testorderservices.client.PatientClient;
import com.datnguyen.testorderservices.client.PatientDTO;
import com.datnguyen.testorderservices.client.UserClient;
import com.datnguyen.testorderservices.dto.request.CommentDeleteRequest;
import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.dto.request.CommentUpdateRequest;
import com.datnguyen.testorderservices.dto.response.*;
import com.datnguyen.testorderservices.entity.*;
import com.datnguyen.testorderservices.exception.DeleteException;
import com.datnguyen.testorderservices.repository.*;
import com.datnguyen.testorderservices.service.CommentService;
import com.datnguyen.testorderservices.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.Jar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    private UserClient userClient;

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

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        try {
            Long jwtDoctorId = jwtUtils.getCurrentUserId();

            RestResponse<UserResponse> clientUser = userClient.getUser(jwtDoctorId);
            if (clientUser == null || clientUser.getData() == null) {
                throw new IllegalArgumentException("Doctor not found");
            }


            if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
                throw new IllegalArgumentException("Comment content cannot be empty.");
            }

            Comment comment = new Comment();
            comment.setDoctorId(clientUser.getData().getId());
            comment.setContent(commentRequest.getContent());
            comment.setStatus(CommentStatus.ACTIVE);


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


            return CommentResponse.builder()
                    .commentId(saved.getId())
                    .doctorName(clientUser.getData().getFullName())
                    .testOrderId(saved.getTestOrder() != null ? saved.getTestOrder().getId() : 0L)
                    .testResultId(saved.getTestResult() != null ? saved.getTestResult().getId() : 0L)
                    .commentContent(saved.getContent())
                    .createdAt(saved.getCreatedAt())
                    .build();

        } catch (Exception e) {
            throw e;
        }
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

            //Lấy thông tin tu nguoiw sửa (Doctor)
            Long jwtDoctorId = jwtUtils.getCurrentUserId();
            RestResponse<UserResponse> clientUser = userClient.getUser(jwtDoctorId);
            if (clientUser == null || clientUser.getData() == null) {
                throw new IllegalArgumentException("Doctor not found");
            }

            //ghi vào auditLog
            AuditLogComment auditLogComment = AuditLogComment.builder()
                    .action("UPDATE_COMMENT")
                    .commentId(comment.getId())
                    .updatedBy(clientUser.getData().getFullName())
                    .oldContent(oldContent)
                    .newContent(request.getContent())
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLogComment);

            //Cập nhật nội dung mới

            comment.setContent(request.getContent());
            comment.setUpdatedBy(clientUser.getData().getFullName());
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);


            return CommentUpdateResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .updatedBy(clientUser.getData().getFullName())
                    .updatedAt(comment.getUpdatedAt())
                    .build();
        } catch (Exception e) {
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
        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new DeleteException("Comment with id " + comment.getId() + " is already deleted.");
        }

        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);

        //3.Ghi vào auditLog
        Long jwtDoctorId = jwtUtils.getCurrentUserId();
        RestResponse<UserResponse> clientUser = userClient.getUser(jwtDoctorId);
        if (clientUser == null) {

            throw new DeleteException("DoctorId not found");
        }
        AuditDeleteComment auditDeleteComment = new AuditDeleteComment();
        auditDeleteComment.setAction(CommentStatus.DELETED.name());
        auditDeleteComment.setReferenceId(comment.getId());
        auditDeleteComment.setEntityType("Comment");
        auditDeleteComment.setPerformedBy(clientUser.getData().getFullName());
        auditDeleteComment.setReason(commentDeleteRequest.getReason());
        auditDeleteComment.setPerformedAt(LocalDateTime.now());

        auditDeleteCommentRepository.save(auditDeleteComment);

        return CommentDeleteResponse.builder()
                .referenceId(comment.getId())
                .action(CommentStatus.DELETED.name())
                .entityType("Comment")
                .performedBy(clientUser.getData().getFullName())
                .reason(commentDeleteRequest.getReason())
                .performedAt(LocalDateTime.now())
                .build();

    }

}
