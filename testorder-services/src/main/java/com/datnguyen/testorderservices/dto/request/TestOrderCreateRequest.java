package com.datnguyen.testorderservices.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestOrderCreateRequest {
    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "createdByUserId is required")
    private Long createdByUserId;
}