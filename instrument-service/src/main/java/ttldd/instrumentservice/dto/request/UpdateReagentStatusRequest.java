package ttldd.instrumentservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ttldd.instrumentservice.entity.ReagentStatus;

@Data
@Builder
public class UpdateReagentStatusRequest {

    private ReagentStatus reagentStatus;

    private int quantity;
}
