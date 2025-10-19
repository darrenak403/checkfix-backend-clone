package ttldd.testorderservices.repository;


import ttldd.testorderservices.entity.TestResultParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultParameterRepository extends JpaRepository<TestResultParameter, Long> {
}