package ttldd.patientservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventLog(EventLogDTO logDTO) {
        try {
            kafkaTemplate.send("event-logs-topic", logDTO);
            log.info("✅ Sent EventLogDTO to Kafka: {}", logDTO);
        } catch (Exception e) {
            log.error("❌ Failed to send EventLogDTO: {}", e.getMessage(), e);
        }
    }
}