package ttldd.testorderservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String doctorName;
    private Long testOrderId;
    private Long testResultId;
    private String commentContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Integer level;
    private Long parentCommentId;
    private List<CommentResponse> replies;
}
