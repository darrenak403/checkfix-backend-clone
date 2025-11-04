package ttldd.monitoringservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String service;       // iam-service, patient-service,...
    private String action;        // CREATE, UPDATE, DELETE
    private String entity;        // User, Role, Patient...
    private String entityId;      // ID đối tượng
    private String performedBy;   // Ai thực hiện
    private String status;        // SUCCESS / ERROR
    @Column(length = 2000)
    private String message;       // Mô tả chi tiết
    private String traceId;       // trace log giữa các service
    private LocalDateTime timestamp;
}
