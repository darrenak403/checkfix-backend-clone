package ttldd.testorderservices.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLog(EventLogDTO event) {
        try {
            kafkaTemplate.send("event-logs-topic", event);
            log.info("📤 Sent EventLog to Kafka: {}", event);
        } catch (Exception e) {
            log.error("❌ Failed to send EventLog to Kafka: {}", e.getMessage());
        }
    }
}