package ttldd.testorderservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.testorderservices.client.PatientClient;
import ttldd.testorderservices.client.UserClient;
import ttldd.testorderservices.dto.request.CommentDeleteRequest;
import ttldd.testorderservices.dto.request.CommentRequest;
import ttldd.testorderservices.dto.request.CommentUpdateRequest;
import ttldd.testorderservices.dto.response.RestResponse;
import ttldd.testorderservices.dto.response.UserResponse;
import ttldd.testorderservices.entity.*;
import ttldd.testorderservices.repository.*;
import ttldd.testorderservices.service.imp.CommentServiceImp;
import ttldd.testorderservices.util.JwtUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImpTest {

    @Mock
    private UserClient userClient;
    @Mock private CommentRepository commentRepository;
    @Mock private PatientClient patientClient;
    @Mock private TestOrderRepository testOrderRepository;
    @Mock private TestResultRepository testResultRepository;
    @Mock private AuditLogCommentRepository auditLogRepository;
    @Mock private AuditDeleteCommentRepository auditDeleteCommentRepository;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private CommentServiceImp commentService;

    @Test
    void addComment_Success_Level1() {
        var request = CommentRequest.builder()
                .testResultId(100L)
                .content("Kết quả ổn định")
                .build();

        when(jwtUtils.getCurrentUserId()).thenReturn(200L);
        when(userClient.getUser(200L)).thenReturn(RestResponse.<UserResponse>builder()
                .data(UserResponse.builder().id(200L).fullName("Dr. Nguyễn").build())
                .build());

        TestOrder testOrder = TestOrder.builder().id(50L).build();
        TestResult result = TestResult.builder().id(100L).testOrder(testOrder).build();

        when(testResultRepository.findById(100L)).thenReturn(Optional.of(result));
        when(testOrderRepository.findById(50L)).thenReturn(Optional.of(testOrder));

        // Create a properly populated saved Comment object
        Comment saved = Comment.builder()
                .id(1L)
                .content("Kết quả ổn định")
                .level(1)
                .testOrder(testOrder)     // Add TestOrder
                .testResult(result)       // Add TestResult
                .doctorName("Dr. Nguyễn") // Add doctor name
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        var response = commentService.addComment(request);

        assertThat(response.getCommentId()).isEqualTo(1L);
        assertThat(response.getLevel()).isEqualTo(1);
        assertThat(response.getDoctorName()).isEqualTo("Dr. Nguyễn");
        assertThat(response.getTestOrderId()).isEqualTo(50L);
        assertThat(response.getTestResultId()).isEqualTo(100L);
    }


    @Test
    void addComment_Reply_Success_Level2() {
        var request = CommentRequest.builder()
                .testResultId(100L)
                .content("Cảm ơn bác sĩ")
                .parentCommentId(5L)
                .build();

        when(jwtUtils.getCurrentUserId()).thenReturn(300L);
        when(userClient.getUser(300L)).thenReturn(RestResponse.success(UserResponse.builder().fullName("Patient").build()));

        // Create TestOrder with ID
        TestOrder testOrder = TestOrder.builder().id(50L).build();
        TestResult result = TestResult.builder()
                .id(100L)
                .testOrder(testOrder)
                .build();

        when(testResultRepository.findById(100L)).thenReturn(Optional.of(result));
        when(testOrderRepository.findById(50L)).thenReturn(Optional.of(testOrder));

        Comment parent = Comment.builder()
                .id(5L)
                .level(1)
                .testResult(result)
                .testOrder(testOrder)
                .status(CommentStatus.ACTIVE)
                .build();
        when(commentRepository.findById(5L)).thenReturn(Optional.of(parent));

        Comment saved = Comment.builder()
                .id(6L)
                .level(2)
                .parentComment(parent)
                .testResult(result)
                .testOrder(testOrder)
                .content("Cảm ơn bác sĩ")
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        var response = commentService.addComment(request);

        assertThat(response.getLevel()).isEqualTo(2);
        assertThat(response.getParentCommentId()).isEqualTo(5L);
        assertThat(response.getTestOrderId()).isEqualTo(50L);
        assertThat(response.getTestResultId()).isEqualTo(100L);
    }

    @Test
    void updateComment_Success_AuditLogged() {
        var request = new CommentUpdateRequest(10L, "Cập nhật: cần theo dõi thêm");

        Comment comment = Comment.builder().id(10L).content("Cũ").build();
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(jwtUtils.getCurrentUserId()).thenReturn(200L);
        when(userClient.getUser(200L)).thenReturn(RestResponse.success(UserResponse.builder().fullName("Dr. A").build()));

        var response = commentService.updateComment(request);

        assertThat(response.getContent()).isEqualTo("Cập nhật: cần theo dõi thêm");
        verify(auditLogRepository).save(argThat(a -> a.getOldContent().equals("Cũ")));
    }

    @Test
    void deleteComment_Success_AuditLogged() {
        var request = new CommentDeleteRequest(15L, "Nội dung không phù hợp");

        Comment comment = Comment.builder().id(15L).status(CommentStatus.ACTIVE).build();
        when(commentRepository.findById(15L)).thenReturn(Optional.of(comment));
        when(jwtUtils.getCurrentUserId()).thenReturn(200L);
        when(userClient.getUser(200L)).thenReturn(RestResponse.success(UserResponse.builder().fullName("Admin").build()));

        var response = commentService.deleteComment(request);

        assertThat(comment.getStatus()).isEqualTo(CommentStatus.DELETED);
        verify(auditDeleteCommentRepository).save(any(AuditDeleteComment.class));
        assertThat(response.getReason()).isEqualTo("Nội dung không phù hợp");
    }
}