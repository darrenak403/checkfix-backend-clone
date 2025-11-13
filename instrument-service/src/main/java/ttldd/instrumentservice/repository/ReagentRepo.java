package ttldd.instrumentservice.repository;

import ttldd.instrumentservice.entity.ReagentEntity;
import ttldd.instrumentservice.entity.ReagentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
//hello
@Repository
public interface ReagentRepo extends MongoRepository<ReagentEntity, String> {
    Optional<ReagentEntity> findByLotNumberAndDeletedFalse(String lotNumber);
    ReagentEntity findFirstByStatusOrderByExpiryDateAsc(ReagentStatus status);
    List<ReagentEntity> findByStatusAndDeletedFalse(ReagentStatus status);

    Optional<ReagentEntity> findByIdAndStatus(String id, ReagentStatus status);

    Optional<ReagentEntity> findByReagentNameAndLotNumber(String name, String lotNumber);

    Optional<ReagentEntity> findByIdAndDeletedFalse(String id);
}
