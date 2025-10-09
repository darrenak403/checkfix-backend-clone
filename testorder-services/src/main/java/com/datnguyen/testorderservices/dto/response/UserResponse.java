package com.datnguyen.testorderservices.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private String email;
    private String fullName;
    private Long id;
    private String role;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
}
