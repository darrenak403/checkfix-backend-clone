package com.datnguyen.monitoringservice.repo;

import com.datnguyen.monitoringservice.entity.EventLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventLogRepository extends MongoRepository<EventLogEntity, String> {

}