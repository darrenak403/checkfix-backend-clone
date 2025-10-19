package ttldd.testorderservices.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.PatientUpdateEvent;
import ttldd.event.dto.UserUpdatedEvent;
import ttldd.testorderservices.service.imp.TestOrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUpdatedConsumer {

    private final TestOrderService testOrderService;

    @KafkaListener(topics = {"sync-user", "patient-updated"})
    public void handleUserUpdated(PatientUpdateEvent event) {
        log.info("Received USER_UPDATED event: {}", event);
        testOrderService.asyncTestOrderFromUser(event);
    }
}
