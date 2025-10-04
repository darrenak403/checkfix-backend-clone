package com.datnguyen.testorderservices.dto.request;

import com.datnguyen.testorderservices.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestOrderUpdateRequest {
    private OrderStatus status;
    private Long runByUserId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime runAt;
}