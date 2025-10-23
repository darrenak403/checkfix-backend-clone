package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.InstrumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface InstrumentRepo extends MongoRepository<InstrumentEntity, UUID> {
}
