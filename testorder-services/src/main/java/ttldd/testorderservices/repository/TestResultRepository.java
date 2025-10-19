package ttldd.testorderservices.repository;

import ttldd.testorderservices.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//public interface TestResultRepository extends JpaRepository<TestResult, Long> {
//    List<TestResult> findByOrderId(Long orderId);
//    Optional<TestResult> findById(Long orderId);
//}

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Optional<TestResult> findByAccessionNumber(String accessionNumber);


//    List<TestResult> findByTestOrder_Id(Long testOrderId);
//
//
//    List<TestResult> findByPatientId(Long patientId);

}