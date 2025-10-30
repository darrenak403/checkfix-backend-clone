package com.datnguyen.instrumentservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BloodAnalysisResponse {
    private String status;
    private String hl7Message;
    private Long testOrderId;
    private String instrumentStatus;
   private Object data;
}
