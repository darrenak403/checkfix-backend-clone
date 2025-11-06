package ttldd.instrumentservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ttldd.instrumentservice.entity.RawHL7TestResult;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RawHL7TestResultRepo  extends MongoRepository<RawHL7TestResult, String> {
        long deleteByCreatedAtBefore(LocalDateTime dateTime);
        boolean existsByAccessionNumber(String accessionNumber);

}
