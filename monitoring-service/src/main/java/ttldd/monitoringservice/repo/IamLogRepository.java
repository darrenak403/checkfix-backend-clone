package ttldd.monitoringservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ttldd.monitoringservice.entity.IamEventLogEntity;

public interface IamLogRepository extends MongoRepository<IamEventLogEntity, String> {
}