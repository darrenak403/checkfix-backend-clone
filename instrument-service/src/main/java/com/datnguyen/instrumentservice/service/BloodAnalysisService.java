package com.datnguyen.instrumentservice.service;

import com.datnguyen.instrumentservice.dto.request.BloodAnalysisRequest;
import com.datnguyen.instrumentservice.dto.response.BloodAnalysisResponse;
import org.springframework.stereotype.Service;

@Service
public interface BloodAnalysisService {
    BloodAnalysisResponse getBloodAnalysisResult(String accessionNumber);
    BloodAnalysisResponse bloodAnalysisHL7(BloodAnalysisRequest bloodAnalysisRequest);
}
