package com.datnguyen.testorderservices.dto.response;

import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.bouncycastle.util.test.TestResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderDetailResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private String gender;
    private String email;
    private String phone;
    private Integer age;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yob;

    private String address;
    private String priority;

    private String testType;

    private String instrument;

    private String createdBy;
    private String runBy;
    private OrderStatus status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime runAt;
    private List<TestResult> results;
    private List<Comment> comments;

}