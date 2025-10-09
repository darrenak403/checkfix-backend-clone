package com.datnguyen.testorderservices.dto.response;

import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.bouncycastle.util.test.TestResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderDetailResponse {

    private Long patientId;
    private String patientName;
    private String gender;
    private String email;
    private Integer age;

    private String createdBy;
    private String runBy;
    private LocalDateTime runAt;
    private OrderStatus status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private List<TestResult> results;
    private List<Comment> comments;

}