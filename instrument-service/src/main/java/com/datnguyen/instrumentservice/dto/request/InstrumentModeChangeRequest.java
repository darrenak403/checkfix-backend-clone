package com.datnguyen.instrumentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentModeChangeRequest {

    @NotBlank
    private String newMode;     // Ready / Maintenance / Inactive

    private String reason;      // Bắt buộc nếu Maintenance / Inactive

    private Boolean qcPassed;   // Bắt buộc nếu Ready

    private String changedBy;   // Người thao tác

}