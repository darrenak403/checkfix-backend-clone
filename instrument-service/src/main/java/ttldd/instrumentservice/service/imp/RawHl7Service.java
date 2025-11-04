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

    // Chạy mỗi 5 giây
    @Scheduled(fixedRate = 5000)
    public void autoDelete() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long deletedCount = rawHL7TestResultRepo.deleteByCreatedAtBefore(oneMinuteAgo);

        log.info("Deleted {} RawHL7TestResult records older than 1 minute", deletedCount);
    }

//    @Scheduled(cron = "0 0 2 * * ?")
//    public void autoDelete() {
//        LocalDateTime thirdTyDay = LocalDateTime.now().minusWeeks(1);
//        long deletedCount = rawHL7TestResultRepo.deleteByCreatedAtBefore(thirdTyDay);
//
//        log.info("Deleted {} RawHL7TestResult records older than 1 minute", deletedCount);
//    }
}
