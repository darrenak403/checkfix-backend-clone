package ttldd.testorderservices.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUpdateRequest {
    private Long id;         // ID của comment cần sửa
    private String content;  // Nội dung mới
}
