package com.datnguyen.testorderservices.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "history_order_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class HistoryOrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    // CREATE / UPDATE / DELETE
    private String action;

    @Lob
    private String detail;

    private Long operatorUserId;
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
