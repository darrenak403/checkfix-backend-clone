package com.datnguyen.instrumentservice.service;

import com.datnguyen.instrumentservice.entity.InstrumentEntity;
import com.datnguyen.instrumentservice.entity.InstrumentMode;
import com.datnguyen.instrumentservice.repository.InstrumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class InstrumentSerivce {
    @Autowired
    private InstrumentRepo instrumentRepo;

    public void saveInstruments() {
        InstrumentEntity instrumentEntity = InstrumentEntity.builder()
                .id(UUID.randomUUID().toString())
                .name("Instrument C")
                .mode(InstrumentMode.READY)
                .build();
        instrumentRepo.save(instrumentEntity);
        System.out.println("✅ Đã lưu instrument vào MongoDB: " + instrumentEntity);
    }
}
