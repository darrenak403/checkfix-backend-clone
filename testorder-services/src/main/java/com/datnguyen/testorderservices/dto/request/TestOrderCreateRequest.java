package com.datnguyen.testorderservices.dto.request;


import com.datnguyen.testorderservices.entity.PriorityStatus;
import jakarta.validation.constraints.NotNull;

import lombok.Data;



@Data
public class TestOrderCreateRequest {
    @NotNull(message = "patientId is required")
    private Long patientId;
    // mức độ ưu tiên :
    private PriorityStatus priority;
    private Long runBy;

}