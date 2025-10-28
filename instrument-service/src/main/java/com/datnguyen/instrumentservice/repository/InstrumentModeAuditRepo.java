package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.InstrumentModeAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
//hello
@Repository
public interface InstrumentModeAuditRepo extends MongoRepository<InstrumentModeAudit, String> {
}
