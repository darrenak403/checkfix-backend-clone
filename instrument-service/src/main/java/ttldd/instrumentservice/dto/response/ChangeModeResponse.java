package ttldd.instrumentservice.dto.response;

import ttldd.instrumentservice.entity.InstrumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
//hello
@Data
@Builder
public class ChangeModeResponse {
    private Long instrumentId;
    private InstrumentStatus previousMode;
    private InstrumentStatus newMode;
    private String changedBy;
    private String reason;
    private LocalDateTime timestamp;
    private String message;
}
