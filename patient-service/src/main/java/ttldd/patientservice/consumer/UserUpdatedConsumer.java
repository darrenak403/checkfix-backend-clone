package ttldd.patientservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.UserUpdatedEvent;
import ttldd.patientservice.entity.Patient;
import ttldd.patientservice.repo.PatientRepo;
import ttldd.patientservice.service.PatientService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUpdatedConsumer {

    private final PatientService patientService;

    @KafkaListener(topics = "user-updated-topic")
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Received USER_UPDATED event: {}", event);
        patientService.asyncPatientFromUser(event);
    }
}
