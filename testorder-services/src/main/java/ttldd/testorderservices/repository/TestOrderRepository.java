package ttldd.testorderservices.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ttldd.testorderservices.entity.TestOrder;
import ttldd.testorderservices.entity.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
    Page<TestOrder> findByDeletedFalse(Pageable pageable);
    Page<TestOrder> findByDeletedFalseAndStatus(OrderStatus status, Pageable pageable);
    Optional<TestOrder> findById(Long testOrderId);
    Page<TestOrder> findByPatientIdAndDeletedFalse(Long id, Pageable pageable);

    List<TestOrder> findByPatientIdAndDeletedFalse(Long id);

    Optional<TestOrder> findByAccessionNumberAndDeletedFalse(String accessionNumber);
    Optional<TestOrder> findByAccessionNumber(String accessionNumber);
    @Query("SELECT o FROM TestOrder o " +
            "LEFT JOIN FETCH o.testResult r " +
            "LEFT JOIN FETCH r.parameters " +
            "WHERE o.accessionNumber = :accessionNumber")
    Optional<TestOrder> findByAccessionNumberWithFullData(@Param("accessionNumber") String accessionNumber);
}
