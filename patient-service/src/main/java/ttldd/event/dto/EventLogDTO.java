package ttldd.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventLogDTO {
    private String service;      // patient-service
    private String action;       // CREATE, UPDATE, DELETE
    private String entity;       // Patient, Appointment, ...
    private String entityId;     // ID đối tượng
    private String performedBy;  // Ai thực hiện
    private String status;       // SUCCESS / ERROR
    private String message;      // Mô tả chi tiết
    private LocalDateTime timestamp; // Thời điểm ghi log
    private String traceId;      // Dùng để truy vết giữa các service
}
