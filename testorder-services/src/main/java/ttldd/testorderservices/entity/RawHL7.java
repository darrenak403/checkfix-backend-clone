package ttldd.testorderservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_hl7")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawHL7 {

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
