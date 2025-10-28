package ttldd.instrumentservice.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "instrument_mode_audit")
public class InstrumentModeAudit {

    @Id
    private String id;

    private String instrumentId;

    private String previousMode;

    private String newMode;

    private String reason;

    private String changedBy;

    private LocalDateTime changedAt;

}