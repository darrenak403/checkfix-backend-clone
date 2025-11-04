package ttldd.testorderservices.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.RawHL7MessageDTO;
import ttldd.testorderservices.entity.RawHL7;
import ttldd.testorderservices.repository.RawHL7Repo;
import ttldd.testorderservices.service.imp.TestResultService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawHl7Consumer {

    private final RawHL7Repo rawHL7LogRepo;

    private final TestResultService testResultService;

    @KafkaListener(
            topics = "raw-hl7-topic"
    )
    public void consume(RawHL7MessageDTO rawMessage) {
        log.info("📥 Received raw HL7 message: {}", rawMessage);

        try {
            testResultService.receiveHl7(rawMessage.getHl7Message());
            log.info("✅ HL7 Log saved: {}", testResultService.receiveHl7(rawMessage.getHl7Message()));
        } catch (Exception e) {
            log.error("❌ Failed to process HL7 log: {}", e.getMessage(), e);
        }
    }
}