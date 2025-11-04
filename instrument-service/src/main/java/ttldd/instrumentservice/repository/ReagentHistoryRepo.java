package ttldd.instrumentservice.repository;

import ttldd.instrumentservice.entity.ReagentHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
//hello
@Repository
public interface ReagentHistoryRepo extends MongoRepository<ReagentHistoryEntity, String> {
}
