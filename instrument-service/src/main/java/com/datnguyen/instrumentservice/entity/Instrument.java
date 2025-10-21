package com.datnguyen.instrumentservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "instruments")
public class Instrument {

    @Id
    private String id;

    private String instrumentName;

    private String mode; // Ready | Maintenance | Inactive


    private boolean lastQcPassed; // true nếu QC pass

    private String reason; // lý do nếu Maintenance/Inactive


    private String lastChangedBy;

    private LocalDateTime lastChangedAt;

}