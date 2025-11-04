package ttldd.instrumentservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventLog(EventLogDTO eventLogDTO) {
        try {
            kafkaTemplate.send("event-logs-topic", eventLogDTO);
            log.info("📤 Sent EventLog to Kafka: {}", eventLogDTO);
        } catch (Exception e) {
            log.error("❌ Failed to send EventLog to Kafka: {}", e.getMessage());
        }
    }
}