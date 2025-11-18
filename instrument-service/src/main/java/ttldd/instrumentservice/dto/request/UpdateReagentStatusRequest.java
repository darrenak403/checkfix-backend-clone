package ttldd.instrumentservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ttldd.instrumentservice.entity.ReagentStatus;

@Data
public class UpdateReagentStatusRequest {
    private String reagentId;


    private ReagentStatus reagentStatus;

    private int quantity;
}
