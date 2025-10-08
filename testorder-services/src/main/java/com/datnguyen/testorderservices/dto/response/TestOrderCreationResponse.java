package com.datnguyen.testorderservices.dto.response;

import com.datnguyen.testorderservices.entity.OrderStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderCreationResponse {
    private Long patientId;

    private String patientName;

    private String email;

    private String address;

    private String phone;

    private String gender;

    private LocalDate yob;
    private Integer  age;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;
}
