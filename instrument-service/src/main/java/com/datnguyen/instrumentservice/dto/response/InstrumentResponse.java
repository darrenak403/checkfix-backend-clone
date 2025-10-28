package com.datnguyen.instrumentservice.dto.response;

import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
//hello
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentResponse {

    Long id;
    String name;
    String serialNumber;
    InstrumentStatus status;
    String createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime createdAt;
}
