package com.datnguyen.instrumentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "instrument_mode_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstrumentModeLogEntity {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "instrument_id", columnDefinition = "uuid", nullable = false)
    private UUID instrumentId;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_mode")
    private InstrumentMode previousMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_mode")
    private InstrumentMode currentMode;

    @Column(name = "reason", length = 2000)
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
       if(createdAt == null) createdAt = LocalDateTime.now();
    }
}
