package ttldd.instrumentservice.repository;

import ttldd.instrumentservice.entity.ReagentAuditLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
//hello
@Repository
public interface ReagentAuditLogRepo extends MongoRepository<ReagentAuditLogEntity, String> {
}
