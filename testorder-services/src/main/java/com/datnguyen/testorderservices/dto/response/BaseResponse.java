package com.datnguyen.testorderservices.dto.response;

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
    private String message;
    private Object data;
    private String path;        // API path
    private String error;
    private LocalDateTime timestamp; // Thời gian xảy ra response
}
