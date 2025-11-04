package ttldd.instrumentservice.dto.response;

import ttldd.instrumentservice.entity.ReagentStatus;
import ttldd.instrumentservice.entity.ReagentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReagentGetAllResponse {
    private String id;
    private ReagentType reagentType;
    private String reagentName;
    private String lotNumber;
    private int quantity;
    private String unit;
    private LocalDate expiryDate;

    private String vendorId;
    private String vendorName;
    private String vendorContact;

    private String installedBy;
    private LocalDate installDate;

    private ReagentStatus status;
    private String remarks;
}
