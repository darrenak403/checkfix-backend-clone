package com.datnguyen.testorderservices.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientDTO {
    private Long id;

    private Long userId;

    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yob;

    private String gender;

    private String address;

    private String phone;

    private String email;

    private Long createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastTestDate;

    private String lastTestType;

    private String instrumentUsed;
    private boolean deleted = false;
}