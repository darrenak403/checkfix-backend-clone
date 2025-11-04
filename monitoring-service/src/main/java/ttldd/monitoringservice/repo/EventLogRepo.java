package ttldd.monitoringservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttldd.monitoringservice.entity.EventLog;

import java.util.List;

@Repository
public interface EventLogRepo extends JpaRepository<EventLog, Long> {
    List<EventLog> findByServiceOrderByTimestampDesc(String service);
}