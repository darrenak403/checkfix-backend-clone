package ttldd.testorderservices.dto.request;

import ttldd.testorderservices.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestOrderUpdateStatusRequest {
    @NotNull
    OrderStatus status;
}
