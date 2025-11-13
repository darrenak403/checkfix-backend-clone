package ttldd.instrumentservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReagentDeleteResponse {
    private String reagentId;
    private String reagentName;
    private String lotNumber;
    private String deletedBy;
    private String action;
    private LocalDateTime deletedAt;
}
