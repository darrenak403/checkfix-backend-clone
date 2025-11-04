package ttldd.instrumentservice.entity;


import lombok.Builder;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Document(collection = "raw_hl7_test")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawHL7TestResult {
    @Id
    private String id;
    private String accessionNumber;
    private String instrumentName;
    private String hl7Message;
    private String generatedBy;
    private String traceId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
