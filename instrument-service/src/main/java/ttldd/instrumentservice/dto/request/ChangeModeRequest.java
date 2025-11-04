package ttldd.instrumentservice.dto.request;

import ttldd.instrumentservice.entity.InstrumentStatus;
import lombok.Data;
//hello
@Data
public class ChangeModeRequest {
    private InstrumentStatus newMode;
    private String reason;
    private Long instrumentId;
    private Boolean qcConfirmed;
}
