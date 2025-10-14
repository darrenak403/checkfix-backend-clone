package com.datnguyen.testorderservices.dto.request;

import com.datnguyen.testorderservices.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TestOrderUpdateRequest {
    private String fullName;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate yob;
    private String gender;
    private String address;
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Phone invalid!!" )
    private String phone;
    private Long runBy;
}