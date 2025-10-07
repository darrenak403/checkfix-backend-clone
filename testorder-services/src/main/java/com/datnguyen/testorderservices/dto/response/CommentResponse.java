package com.datnguyen.testorderservices.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private long commentId;
    private String userName;
    private long testOrderId;
    private long testResultId;
    private String commentContent;
    private LocalDateTime createdAt;
}
