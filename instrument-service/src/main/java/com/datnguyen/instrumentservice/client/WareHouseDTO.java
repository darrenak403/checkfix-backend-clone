package com.datnguyen.instrumentservice.client;

import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
@Data
public class WareHouseDTO {

    private Long id;

    private String name;

    private String serialNumber;

    private InstrumentStatus status;

    private boolean isActive = true;

    private Instant deactivatedAt;

    private String createdBy;

    private LocalDateTime createdAt;
}
