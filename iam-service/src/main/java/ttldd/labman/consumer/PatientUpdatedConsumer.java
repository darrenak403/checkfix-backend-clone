package ttldd.labman.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.PatientUpdateEvent;
import ttldd.event.dto.UserUpdatedEvent;
import ttldd.labman.repo.UserRepo;
import ttldd.labman.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientUpdatedConsumer {

    private final UserService userService;

    private final UserRepo userRepo;

    @KafkaListener(topics = "patient-updated")
    public void handlePatientUpdated(PatientUpdateEvent event) {
        log.info("Received PatientUpdatedEvent: {}", event);
        userService.syncUserFromPatient(event);
    }
}
