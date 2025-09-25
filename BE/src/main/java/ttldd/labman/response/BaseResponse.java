package ttldd.labman.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResponse {
    private int status;
    private String message;
    private Object data;
    private String path;        // API path
    private LocalDateTime timestamp; // Thời gian xảy ra response
}
