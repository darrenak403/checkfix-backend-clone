package com.datnguyen.testorderservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;         // VD: "UPDATE_COMMENT"
    private Long commentId;        // ID bình luận
    private String updatedBy;      // Người chỉnh sửa
    @Column(columnDefinition = "TEXT")
    private String oldContent;     // Nội dung cũ
    @Column(columnDefinition = "TEXT")
    private String newContent;     // Nội dung mới
    private LocalDateTime timestamp;
}
