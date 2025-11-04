package ttldd.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttldd.monitoringservice.entity.EventLog;
import ttldd.monitoringservice.repo.EventLogRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepo eventLogRepo;

    public List<EventLog> getAllLogs() {
        return eventLogRepo.findAll();
    }

    public List<EventLog> getLogsByService(String service) {
        return eventLogRepo.findByServiceOrderByTimestampDesc(service);
    }
}