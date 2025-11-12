package ttldd.instrumentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ttldd.instrumentservice.entity.ReagentStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateReagentStatusResponse {
    private String reagentName;
    private ReagentStatus oldStatus;
    private ReagentStatus newStatus;
    private int oldQuantity;
    private int newQuantity;
    private String updatedBy;
    private LocalDateTime timestamp;
}
