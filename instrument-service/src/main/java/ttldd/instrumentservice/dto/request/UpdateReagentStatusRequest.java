package ttldd.instrumentservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ttldd.instrumentservice.entity.ReagentStatus;

@Data
public class UpdateReagentStatusRequest {
    private String reagentName;

    @NotBlank(message = "Lot number is required")
    private String lotNumber;

    @NotNull(message = "AVAILABLE status is required")
    private ReagentStatus reagentStatus;

    @NotNull(message = "Quantity is required")
    @Min(value = 5, message = "Invalid quantity value")
    private int quantity;
}
