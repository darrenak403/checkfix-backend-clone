package ttldd.monitoringservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttldd.monitoringservice.entity.RawHL7Log;

@Repository
public interface RawHL7LogRepo extends JpaRepository<RawHL7Log, Long> {
}