package ttldd.instrumentservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BloodAnalysisRequest {
    private String accessionNumber;
    private String reagentId;
}
