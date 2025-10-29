package ttldd.monitoringservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "iam_errors")
public class IamErrorLogEntity {
    @Id
    private String id;
    private String serviceName;
    private String user;
    private String action;
    private String description;
    private String exception;
    private String stackTrace;
    private String path;
    private String ip;
    private String requestId;
    private LocalDateTime timestamp;
}