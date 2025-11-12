package ttldd.instrumentservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ttldd.instrumentservice.entity.ReagentUpdateAuditLogEntity;

@Repository
public interface ReagentUpdateAuditLogRepo extends MongoRepository<ReagentUpdateAuditLogEntity, String> {
}
