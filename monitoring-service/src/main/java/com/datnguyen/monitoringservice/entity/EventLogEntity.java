package com.datnguyen.monitoringservice.entity;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "logs")
public class EventLogEntity {
    @Id
    private String id;
    private String serviceName;
    private String eventType;
    private String description;
    private String level;
    private int status;
    private long latency;
    private LocalDateTime timestamp;
}