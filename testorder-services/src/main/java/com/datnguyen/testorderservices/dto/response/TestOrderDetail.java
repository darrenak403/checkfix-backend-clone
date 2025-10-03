package com.datnguyen.testorderservices.dto.response;

import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.datnguyen.testorderservices.entity.TestResult;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderDetail {
    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;

    private Long patientId;
    private String patientName;
    private String patientGender;
    private String patientEmail;
    private Integer patientAge;

    private Long createdByUserId;
    private Long runByUserId;
    private LocalDateTime runAt;

    private List<TestResult> results;
    private List<Comment> comments;
}