package ttldd.event.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventLogDTO {
    private String service;
    private String action;
    private String entity;
    private String entityId;
    private String performedBy;
    private String status;
    private String message;
    private String traceId;
    private LocalDateTime timestamp;
}