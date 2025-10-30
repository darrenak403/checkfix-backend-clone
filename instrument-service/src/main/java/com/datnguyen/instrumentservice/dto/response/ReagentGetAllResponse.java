package com.datnguyen.instrumentservice.dto.response;

import com.datnguyen.instrumentservice.entity.ReagentStatus;
import com.datnguyen.instrumentservice.entity.ReagentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReagentGetAllResponse {
    private ReagentType reagentType;
    private String reagentName;
    private String lotNumber;
    private int quantity;
    private String unit;
    private LocalDate expiryDate;

    private String vendorId;
    private String vendorName;
    private String vendorContact;

    private String installedBy;
    private LocalDate installDate;

    private ReagentStatus status;
    private String remarks;
}
