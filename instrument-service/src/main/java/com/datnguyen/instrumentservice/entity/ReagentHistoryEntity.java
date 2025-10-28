package com.datnguyen.instrumentservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
//hello
@Document(collection = "reagent_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReagentHistoryEntity {
    @Id
    private String id;

    private String reagentId;
    private ReagentType reagentType;
    private String reagentName;
    private String lotNumber;
    private int quantity;
    private String unit;
    private LocalDate expiryDate;

    private String vendorId;
    private String vendorName;

    private String installedBy;
    private LocalDateTime installTimestamp;

    private String action; // INSTALL, UPDATE, REMOVE
    private String remarks; // ghi chú
}
