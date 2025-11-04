package ttldd.instrumentservice.dto.response;

import ttldd.instrumentservice.entity.ReagentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
//hello
@Data
@Builder
public class ReagentInstallResponse {
    private String reagentId;
    private ReagentType reagentType;
    private String reagentName;
    private String lotNumber; // lô thuốc thử
    private int quantity;
    private String unit;
    private LocalDate expiryDate;
    private String vendorId;
    private String vendorName;
    private String installedBy;
    private LocalDate installDate;
    private String status;
}
