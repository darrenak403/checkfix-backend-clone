package com.datnguyen.testorderservices.dto.request;

import lombok.Data;

@Data
public class CommentDeleteRequest {
    private Long commentId;
    private Long deleteById;
    private String reason;
}
