package ttldd.monitoringservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ttldd.monitoringservice.entity.IamErrorLogEntity;

public interface IamErrorLogRepository extends MongoRepository<IamErrorLogEntity, String> {
}