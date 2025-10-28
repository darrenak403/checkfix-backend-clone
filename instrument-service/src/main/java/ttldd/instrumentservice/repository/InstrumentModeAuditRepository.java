package ttldd.instrumentservice.repository;

import ttldd.instrumentservice.entity.InstrumentModeAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentModeAuditRepo extends MongoRepository<InstrumentModeAudit, String> {
}
