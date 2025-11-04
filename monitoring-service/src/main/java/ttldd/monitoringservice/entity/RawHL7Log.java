package ttldd.monitoringservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "raw_hl7_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawHL7Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessionNumber;
    private String instrumentName;
    @Column(columnDefinition = "TEXT")
    private String hl7Message;
    private String filePath;
    private String generatedBy;
    private LocalDateTime timestamp;
    private String traceId;
}
