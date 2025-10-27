package com.datnguyen.instrumentservice.dto.request;

import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import lombok.Data;

@Data
public class ChangeModeRequest {
    private InstrumentStatus newMode;
    private String reason;
    private Long instrumentId;
    private Boolean qcConfirmed;
}
