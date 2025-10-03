package com.datnguyen.testorderservices.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TestOrder order;


    private String parameter;

    private String value;


    //Flag: NORMAL / ABNORMAL / CRITICAL
    private String flag;
    @Lob
    private String hl7Raw;

    private LocalDateTime createdAt;
    @PrePersist
    void prePersist() {

        if (getOrder().getCreatedAt() == null) createdAt = LocalDateTime.now();
    }
}
