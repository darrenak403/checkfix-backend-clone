package ttldd.warehouseservice.dto.request;

import lombok.Builder;
import ttldd.warehouseservice.entity.InstrumentStatus;
import lombok.Data;

@Data
@Builder
public class InstrumentUpdateRequest {
    private InstrumentStatus status;
}
