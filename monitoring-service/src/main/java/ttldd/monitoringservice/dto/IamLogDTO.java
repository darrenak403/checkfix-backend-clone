package ttldd.monitoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ttldd.monitoringservice.entity.IamEventLogEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IamLogDTO {
    private String serviceName;
    private String user;
    private String action;
    private String description;
    private String status;
    private String type;        // INFO | ERROR | WARN
    private String method;      // GET/POST/PUT/DELETE
    private String path;        // URI endpoint
    private String ip;          // IP người dùng
    private String requestId;   // UUID trace ID
    private String exception;   // tên exception (nếu có)
    private String stackTrace;  // chi tiết stacktrace
    private LocalDateTime timestamp;
}