package com.datnguyen.testorderservices.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Builder
@Data
public class CommentUpdateResponse {
    private Long id;
    private String content;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
