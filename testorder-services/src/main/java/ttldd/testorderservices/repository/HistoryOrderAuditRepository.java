package ttldd.testorderservices.repository;

import ttldd.testorderservices.entity.HistoryOrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryOrderAuditRepository extends JpaRepository<HistoryOrderAudit, Long> {


}