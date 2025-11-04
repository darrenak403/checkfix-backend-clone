package ttldd.instrumentservice.service;

import ttldd.instrumentservice.dto.request.BloodAnalysisRequest;
import ttldd.instrumentservice.dto.response.BloodAnalysisResponse;
import org.springframework.stereotype.Service;

@Service
public interface BloodAnalysisService {
    BloodAnalysisResponse getBloodAnalysisResult(String accessionNumber);
    BloodAnalysisResponse bloodAnalysisHL7(BloodAnalysisRequest bloodAnalysisRequest);
}
