package ttldd.instrumentservice.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ttldd.instrumentservice.repository.RawHL7TestResultRepo;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawHl7Service {

    private final RawHL7TestResultRepo rawHL7TestResultRepo;

    @Scheduled(cron = "0 0 2 * * ?")
    public void autoDelete() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusDays(30);
        long deletedCount = rawHL7TestResultRepo.deleteByCreatedAtBefore(oneMinuteAgo);

        log.info("Deleted {} RawHL7TestResult records older than 1 minute", deletedCount);
    }
}
