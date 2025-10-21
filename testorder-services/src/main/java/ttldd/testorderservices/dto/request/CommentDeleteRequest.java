package ttldd.testorderservices.dto.request;

import lombok.Data;

@Data
public class CommentDeleteRequest {
    private Long commentId;
    private String reason;
}
