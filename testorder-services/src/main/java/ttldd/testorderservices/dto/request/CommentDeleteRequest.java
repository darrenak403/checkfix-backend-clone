package ttldd.testorderservices.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDeleteRequest {
    private Long commentId;
    private String reason;
}
