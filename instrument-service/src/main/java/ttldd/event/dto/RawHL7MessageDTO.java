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
public class RawHL7MessageDTO {
    private String service;         // "instrument-service"
    private String accessionNumber; // Mã xét nghiệm
    private String testOrderId;     // ID đơn xét nghiệm
    private String instrumentName;  // Tên máy (nếu có)
    private String hl7Message;      // Nội dung HL7 raw
    private String generatedBy;     // Người thực hiện
    private LocalDateTime timestamp;
    private String traceId;         // trace ID để liên kết log
}