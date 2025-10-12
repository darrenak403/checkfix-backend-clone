package com.datnguyen.testorderservices.dto.response;

import com.datnguyen.testorderservices.entity.OrderStatus;
import com.datnguyen.testorderservices.entity.PriorityStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TestOrderResponse {
    private Long id;

    private Long patientId;

    private String patientName;

    private String email;

    private String address;

    private String phone;

    private String gender;

    private LocalDate yob;
    private Integer  age;

    private String priority;

    private String testType;

    private String instrument;

    private String createdBy;

    private String runBy;

    private String status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
