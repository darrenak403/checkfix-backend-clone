package com.datnguyen.testorderservices.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private long commentId;
    private String doctorName;
    private long testOrderId;
    private long testResultId;
    private String commentContent;
    private LocalDateTime createdAt;
}
