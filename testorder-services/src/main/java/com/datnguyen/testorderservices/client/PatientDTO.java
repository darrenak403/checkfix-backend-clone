package com.datnguyen.testorderservices.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientDTO {
    private Long id;

    private Long userId;

    private String patientCode;

    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yob;

    private String gender;

    private String address;

    private String avatarUrl;

    private String phone;

    private String email;

    private String createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    private String modifiedBy;

}