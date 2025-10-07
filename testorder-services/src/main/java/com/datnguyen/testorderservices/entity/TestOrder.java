package com.datnguyen.testorderservices.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table (name = "test_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TestOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private String patientName;

    private String email;

    private String address;

    private String phone;

    private String gender;

    private LocalDate yob;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    private Long createdByUserId;

    // time tạo phiếu xét nghiệm
    @Column(nullable = false)
    private LocalDateTime createdAt;


    // xét nghiệm lúc nào
    private LocalDateTime runAt;


    private Boolean deleted = false;
    private Long deletedByUserId;
    private LocalDateTime deletedAt;


    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = OrderStatus.PENDING;
    }


}
