package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.ReagentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReagentRepo extends MongoRepository<ReagentEntity, String> {
    Optional<ReagentEntity> findByLotNumber(String lotNumber);
}
