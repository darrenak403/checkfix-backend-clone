package ttldd.instrumentservice.dto.request;

import lombok.Data;

@Data
public class BloodAnalysisRequest {
    private String accessionNumber;
    private String reagentId;
}
