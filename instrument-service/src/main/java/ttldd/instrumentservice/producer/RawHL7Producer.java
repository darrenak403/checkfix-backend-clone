package ttldd.instrumentservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ttldd.event.dto.RawHL7MessageDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawHL7Producer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRawHL7(RawHL7MessageDTO rawHL7MessageDTO) {
        try {
//            kafkaTemplate.send("raw-hl7-topic", rawHL7MessageDTO);
            ProducerRecord<String, Object> record = new ProducerRecord<>("raw-hl7-topic", rawHL7MessageDTO);
            log.info("Headers before send: {}", record.headers());
            kafkaTemplate.send("raw-hl7-topic", rawHL7MessageDTO);
            log.info("📤 Sent raw HL7 message to Kafka: {}", rawHL7MessageDTO.getAccessionNumber());
        } catch (Exception e) {
            log.error("❌ Failed to send HL7 to Kafka: {}", e.getMessage(), e);
        }
    }
}