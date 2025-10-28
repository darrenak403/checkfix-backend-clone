package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.ReagentHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
//hello
@Repository
public interface ReagentHistoryRepo extends MongoRepository<ReagentHistoryEntity, String> {
}
