package ttldd.testorderservices.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ttldd.event.dto.NotificationEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmail(String topic, String recipient, String subject,
                          String templateCode, Map<String, Object> params) {
        NotificationEvent event = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(recipient)
                .subject(subject)
                .templateCode(templateCode)
                .param(params)
                .build();

        kafkaTemplate.send(topic, event);
        log.info("✅ Sent email event to Kafka: {}", event);
    }
}
