package ttldd.testorderservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private long commentId;
    private String doctorName;
    private long testOrderId;
    private long testResultId;
    private String commentContent;
    private LocalDateTime createdAt;
}
