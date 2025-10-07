package com.datnguyen.testorderservices.dto.request;

import lombok.Data;

@Data
public class CommentUpdateRequest {
    private Long id;         // ID của comment cần sửa
    private String content;  // Nội dung mới
    private String updatedBy; // Người chỉnh sửa
}
