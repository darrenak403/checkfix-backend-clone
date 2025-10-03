package com.datnguyen.testorderservices.repository;

import com.datnguyen.testorderservices.entity.HistoryOrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryOrderAuditRepository extends JpaRepository<HistoryOrderAudit, Long> {


}