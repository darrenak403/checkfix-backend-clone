package ttldd.monitoringservice.repo;

import ttldd.monitoringservice.entity.EventLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventLogRepository extends MongoRepository<EventLogEntity, String> {

}