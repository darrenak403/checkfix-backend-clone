package com.datnguyen.monitoringservice.service;

import com.datnguyen.monitoringservice.entity.ApiLogDTO;
import com.datnguyen.monitoringservice.entity.EventLogEntity;
import com.datnguyen.monitoringservice.repo.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogConsumer {

    private final EventLogRepository repository;

    @KafkaListener(topics = "api-logs", groupId = "monitoring-group")
    public void consume(ApiLogDTO log) {
        EventLogEntity entity = EventLogEntity.builder()
                .serviceName(log.getServiceName())
                .description(String.format("%s %s -> %d (%d ms)", log.getMethod(), log.getPath(), log.getStatus(), log.getLatency()))
                .status(log.getStatus())
                .latency(log.getLatency())
                .timestamp(log.getTimestamp())
                .build();

        repository.save(entity);
        System.out.println("📥 [Monitoring] Saved log: " + entity);
    }
}