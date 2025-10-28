package com.datnguyen.instrumentservice.dto.request;


import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import lombok.Data;
//hello
@Data
public class InstrumentUpdateRequest {
    private InstrumentStatus status;
}
