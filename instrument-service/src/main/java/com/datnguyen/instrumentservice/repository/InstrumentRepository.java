package com.datnguyen.instrumentservice.repository;

import com.datnguyen.instrumentservice.entity.Instrument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends MongoRepository<Instrument, String> {

}