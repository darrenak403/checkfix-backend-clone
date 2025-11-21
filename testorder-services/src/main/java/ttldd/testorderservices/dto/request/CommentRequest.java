package ttldd.testorderservices.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private Long testOrderId;
    private Long parentCommentId;
    private String content;
}
