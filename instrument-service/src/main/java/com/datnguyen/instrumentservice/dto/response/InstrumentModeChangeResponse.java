package com.datnguyen.instrumentservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentModeChangeResponse {

    private String instrumentId;

    private String oldMode;

    private String newMode;

    private String reason;

    private String changedBy;

    private LocalDateTime changedAt;

}