package com.datnguyen.testorderservices.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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


    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;


    // time tạo phiếu xét nghiệm
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PriorityStatus priority;

    private String testType;

    private String instrument;

    // xét nghiệm lúc nào
    private LocalDateTime runAt;
    private String runBy ;

    private String createdBy;


    private Boolean deleted = false;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = OrderStatus.PENDING;
    }

    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL)
    private List<Comment> comments;
}
