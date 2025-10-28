package ttldd.instrumentservice.repository;

<<<<<<<< HEAD:instrument-service/src/main/java/ttldd/instrumentservice/repository/ReagentHistoryRepo.java
import com.datnguyen.instrumentservice.entity.ReagentHistoryEntity;
========
import ttldd.instrumentservice.entity.Instrument;
>>>>>>>> origin/50-be-implement-monitoring-service:instrument-service/src/main/java/ttldd/instrumentservice/repository/InstrumentRepository.java
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReagentHistoryRepo extends MongoRepository<ReagentHistoryEntity, String> {
}
