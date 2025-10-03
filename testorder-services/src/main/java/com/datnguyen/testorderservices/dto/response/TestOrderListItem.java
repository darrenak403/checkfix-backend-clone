package com.datnguyen.testorderservices.dto.response;

import com.datnguyen.testorderservices.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderListItem {

    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String phone ;
    private Long patientId;
    private String patientName;
    private Integer patientAge;
    private String patientGender;

}