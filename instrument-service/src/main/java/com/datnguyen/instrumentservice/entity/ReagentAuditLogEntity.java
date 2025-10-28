package com.datnguyen.instrumentservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
//xin chao
@Document(collection = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentAuditLogEntity {
    @Id
    private String id;

    private String username;
    private String action;
    private String reagentName;
    private String reagentId;
    private LocalDateTime timestamp;
    private String description;
}
