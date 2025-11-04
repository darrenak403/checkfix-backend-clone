package ttldd.warehouseservice.dto.request;

import ttldd.warehouseservice.entity.InstrumentStatus;
import lombok.Data;

@Data
public class InstrumentUpdateRequest {
    private InstrumentStatus status;
}
