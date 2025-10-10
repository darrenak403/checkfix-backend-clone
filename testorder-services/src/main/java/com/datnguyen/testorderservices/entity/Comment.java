package com.datnguyen.testorderservices.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctorId", nullable = false)
    private Long doctorId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "status")
    private String status;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // Tự động gán thời gian khi lưu mới
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_id", nullable = true)
    private TestOrder testOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_result_id", nullable = true)
    private TestResult testResult;
}