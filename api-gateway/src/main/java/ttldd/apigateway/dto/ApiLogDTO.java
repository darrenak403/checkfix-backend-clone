package ttldd.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLogDTO {
    private String serviceName;
    private String method;
    private String path;
    private int status;
    private long latency;
    private LocalDateTime timestamp;
}