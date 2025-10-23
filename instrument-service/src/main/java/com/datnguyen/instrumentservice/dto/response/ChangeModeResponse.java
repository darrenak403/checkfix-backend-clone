package com.datnguyen.instrumentservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChangeModeResponse {
    private Long instrumentId;
    private String previousMode;
    private String newMode;
    private String changedBy;
    private String reason;
    private LocalDateTime timestamp;
    private String message;
}
