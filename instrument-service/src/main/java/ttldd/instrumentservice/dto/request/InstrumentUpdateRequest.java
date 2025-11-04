package ttldd.instrumentservice.dto.request;


import ttldd.instrumentservice.entity.InstrumentStatus;
import lombok.Data;
//hello
@Data
public class InstrumentUpdateRequest {
    private InstrumentStatus status;
}
