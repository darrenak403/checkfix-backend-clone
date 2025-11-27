package ttldd.testorderservices.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequest {
    private Long testResultId;
    private Long parentCommentId;
    private String content;
}
