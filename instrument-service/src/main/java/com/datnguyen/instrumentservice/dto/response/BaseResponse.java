package com.datnguyen.instrumentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResponse {
    private int status;
    private String error;
    private String message;
    private String path;        // API path
    private LocalDateTime timestamp; // Thời gian xảy ra response
    private Object data;
}
