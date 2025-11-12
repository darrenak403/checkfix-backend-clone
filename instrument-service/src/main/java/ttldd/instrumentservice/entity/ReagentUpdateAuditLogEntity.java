package ttldd.instrumentservice.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reagent_update_audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentUpdateAuditLogEntity {

    private String id;

    private String reagentName;
    private String updatedBy;
    private ReagentStatus oldStatus;
    private ReagentStatus newStatus;
    private int oldValue;
    private int newValue;

    private LocalDateTime timestamp;
}
