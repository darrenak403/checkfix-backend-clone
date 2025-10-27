package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.ReagentAuditLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReagentAuditLogRepo extends MongoRepository<ReagentAuditLogEntity, String> {
}
