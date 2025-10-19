package ttldd.testorderservices.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private Long testOrderId;
    private Long testResultId;
    private String content;
}
