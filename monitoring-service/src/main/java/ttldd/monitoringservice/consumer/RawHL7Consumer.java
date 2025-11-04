package ttldd.monitoringservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.RawHL7MessageDTO;
import ttldd.monitoringservice.entity.RawHL7Log;
import ttldd.monitoringservice.repo.RawHL7LogRepo;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawHL7Consumer {

    private final RawHL7LogRepo rawHL7LogRepo;

    @KafkaListener(
            topics = "raw-hl7-topic"
//            groupId = "monitoring-group"

    )
    public void consume(RawHL7MessageDTO rawMessage) {
        log.info("📥 Received raw HL7 message: {}", rawMessage);

        try {
            RawHL7Log entity = RawHL7Log.builder()
                    .accessionNumber(rawMessage.getAccessionNumber())
                    .instrumentName(rawMessage.getInstrumentName())
                    .hl7Message(rawMessage.getHl7Message())
                    .generatedBy(rawMessage.getGeneratedBy())
                    .timestamp(rawMessage.getTimestamp())
                    .traceId(rawMessage.getTraceId())
                    .build();

            rawHL7LogRepo.save(entity);
            log.info("✅ HL7 Log saved: {}", entity.getAccessionNumber());
        } catch (Exception e) {
            log.error("❌ Failed to process HL7 log: {}", e.getMessage(), e);
        }
    }
}