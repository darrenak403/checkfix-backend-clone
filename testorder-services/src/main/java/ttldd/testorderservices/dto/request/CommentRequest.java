package ttldd.testorderservices.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private Long testResultId;
    private Long parentCommentId;
    private String content;
}
