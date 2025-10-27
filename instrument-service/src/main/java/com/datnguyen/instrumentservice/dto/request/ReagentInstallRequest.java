package com.datnguyen.instrumentservice.dto.request;

import com.datnguyen.instrumentservice.entity.ReagentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReagentInstallRequest {
    @NotNull(message = "Reagent type is required")
    private ReagentType reagentType;

    @NotBlank
    private String reagentName;

    @NotBlank
    private String lotNumber;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    @NotBlank
    private String unit;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @NotBlank
    private String vendorId;

    @NotBlank
    private String vendorName;

    private String vendorContact;
    private String instrumentId;

    @NotBlank
    private String installedBy;

    private String remarks;
}
