package ttldd.testorderservices.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class CommentDeleteResponse {
    private String action; // "DELETE_COMMENT"
    private Long referenceId; // ID của comment bị xóa
    private String entityType; // "Comment"
    private String performedBy; // username
    private String reason;
    private LocalDateTime performedAt;
}
