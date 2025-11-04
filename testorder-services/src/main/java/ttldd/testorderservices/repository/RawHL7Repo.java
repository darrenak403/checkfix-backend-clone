package ttldd.testorderservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttldd.testorderservices.entity.RawHL7;

@Repository
public interface RawHL7Repo extends JpaRepository<RawHL7, Long> {
}