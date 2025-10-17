package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.InstrumentModeAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentModeAuditRepository extends MongoRepository<InstrumentModeAudit, String> {
    List<InstrumentModeAudit> findByInstrumentIdOrderByChangedAtDesc(String instrumentId);
}