package jungle.warehouseservice.dto.request;

import jungle.warehouseservice.entity.InstrumentStatus;
import lombok.Data;

@Data
public class InstrumentUpdateRequest {
    private InstrumentStatus status;
}
