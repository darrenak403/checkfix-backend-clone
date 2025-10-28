package ttldd.labman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEventDTO {
    private String serviceName;      // iam-service
    private String user;             // username hoặc "anonymous"
    private String action;           // LOGIN, REGISTER, UPDATE_USER,...
    private String description;      // mô tả hành động
    private String status;           // SUCCESS | FAILED | ERROR
    private String type;             // INFO | WARN | ERROR
    private String method;           // POST/GET/PUT/DELETE
    private String path;             // /api/auth/login
    private String ip;               // 127.0.0.1
    private String requestId;        // UUID để trace
    private String exception;        // tên exception (nếu có)
    private String stackTrace;       // stacktrace chi tiết (nếu lỗi)
    private LocalDateTime timestamp; // thời gian log
}