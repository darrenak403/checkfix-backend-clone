package ttldd.monitoringservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;
import ttldd.monitoringservice.entity.EventLog;
import ttldd.monitoringservice.repo.EventLogRepo;

import java.time.LocalDateTime;
import java.util.Map;

//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class EventLogConsumer {
//
//    private final EventLogRepo eventLogRepo;
//
//    @KafkaListener(topics = "event-logs-topic", groupId = "monitoring-group")
//    public void consume(EventLogDTO event) {
//        log.info("📥 Received EventLog from Kafka: {}", event);
//
//        try {
//            EventLog entity = EventLog.builder()
//                    .service(event.getService())
//                    .action(event.getAction())
//                    .entity(event.getEntity())
//                    .entityId(event.getEntityId())
//                    .performedBy(event.getPerformedBy())
//                    .status(event.getStatus())
//                    .message(event.getMessage())
//                    .traceId(event.getTraceId())
//                    .timestamp(event.getTimestamp())
//                    .build();
//
//            eventLogRepo.save(entity);
//            log.info("✅ Log saved to DB for service: {}", event.getService());
//        } catch (Exception e) {
//            log.error("❌ Failed to save EventLog to DB: {}", e.getMessage());
//        }
//    }
//}

@Slf4j
@Component
@RequiredArgsConstructor
public class EventLogConsumer {

    private final EventLogRepo eventLogRepo;

    @KafkaListener(topics = "event-logs-topic"
//            groupId = "monitoring-group"
    )
    public void consume(EventLogDTO event) {
        log.info("📥 Received EventLog from Kafka: {}", event);

        try {
            EventLog entity = EventLog.builder()
                    .service(event.getService())
                    .action(event.getAction())
                    .entity(event.getEntity())
                    .entityId(event.getEntityId())
                    .performedBy(event.getPerformedBy())
                    .status(event.getStatus())
                    .message(event.getMessage())
                    .traceId(event.getTraceId())
                    .timestamp(event.getTimestamp())
                    .build();

            eventLogRepo.save(entity);
            log.info("✅ Log saved to DB for service: {}", event.getService());
        } catch (Exception e) {
            log.error("❌ Failed to save EventLog to DB: {}", e.getMessage(), e);
        }
    }
}