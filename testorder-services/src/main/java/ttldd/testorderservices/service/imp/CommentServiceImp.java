package ttldd.testorderservices.service.imp;

import ttldd.testorderservices.client.PatientClient;
import ttldd.testorderservices.client.UserClient;
import ttldd.testorderservices.dto.request.CommentDeleteRequest;
import ttldd.testorderservices.dto.request.CommentRequest;
import ttldd.testorderservices.dto.request.CommentUpdateRequest;
import ttldd.testorderservices.dto.response.*;
import ttldd.testorderservices.entity.*;
import ttldd.testorderservices.exception.DeleteException;
import ttldd.testorderservices.repository.*;
import ttldd.testorderservices.service.CommentService;
import ttldd.testorderservices.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

            // Tìm TestResult theo testResultId
            TestResult result = testResultRepository.findById(commentRequest.getTestResultId())
                    .orElseThrow(() -> new IllegalArgumentException("Test Result not found"));

            // Lấy TestOrder từ TestResult
            TestOrder testOrder = testOrderRepository.findById(result.getTestOrder().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Test Order not found"));

            comment.setTestResult(result);
            comment.setTestOrder(testOrder);
            comment.setDoctorId(clientUser.getData().getId());
            comment.setContent(commentRequest.getContent());
            comment.setStatus(CommentStatus.ACTIVE);
            comment.setTestResult(result);
            comment.setTestOrder(testOrder);

            // Kiểm tra nếu là reply
            if (commentRequest.getParentCommentId() != null) {
                Comment parent = commentRepository.findById(commentRequest.getParentCommentId())
                        .orElseThrow(() -> new RuntimeException("Parent comment not found."));

                // Validation: Parent comment phải cùng testResult
                if (!parent.getTestResult().getId().equals(result.getId())) {
                    throw new RuntimeException("Cannot reply to comment from different test result.");
                }

                // Validation: Parent comment phải ACTIVE
                if (parent.getStatus() != CommentStatus.ACTIVE) {
                    throw new RuntimeException("Cannot reply to inactive or deleted comment.");
                }

                if (parent.getLevel() >= 2) {
                    throw new RuntimeException("Only two levels of comments are allowed.");
                }

                comment.setParentComment(parent);
                comment.setLevel(parent.getLevel() + 1);
            } else {
                comment.setLevel(1);
            }

            Comment saved = commentRepository.save(comment);

            return CommentResponse.builder()
                    .commentId(saved.getId())
                    .doctorName(clientUser.getData().getFullName())
                    .testOrderId(saved.getTestOrder().getId())
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
        // Kiểm tra tồn tại
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + request.getId()));

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty.");
        }
        // Luu nội dung cũ
        try {
            String oldContent = comment.getContent();

            // Lấy thông tin tu nguoiw sửa (Doctor)
            Long jwtDoctorId = jwtUtils.getCurrentUserId();
            RestResponse<UserResponse> clientUser = userClient.getUser(jwtDoctorId);
            if (clientUser == null || clientUser.getData() == null) {
                throw new IllegalArgumentException("Doctor not found");
            }

            // ghi vào auditLog
            AuditLogComment auditLogComment = AuditLogComment.builder()
                    .action("UPDATE_COMMENT")
                    .commentId(comment.getId())
                    .updatedBy(clientUser.getData().getFullName())
                    .oldContent(oldContent)
                    .newContent(request.getContent())
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLogComment);

            // Cập nhật nội dung mới

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

        // 1.Tìm comment
        Comment comment = commentRepository.findById(commentDeleteRequest.getCommentId())
                .orElseThrow(
                        () -> new DeleteException("Comment not found with id: " + commentDeleteRequest.getCommentId()));

        // 2.Kiểm tra status comment đã xóa chưa
        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new DeleteException("Comment with id " + comment.getId() + " is already deleted.");
        }

        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);

        // 3.Ghi vào auditLog
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
