package com.datnguyen.testorderservices.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_delete_comments")
@Data

public class AuditDeleteComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String action; // "DELETE_COMMENT"
    private Long referenceId; // ID của comment bị xóa
    private String entityType; // "Comment"
    private String performedBy; // username
    private String reason;
    private LocalDateTime performedAt;
}
