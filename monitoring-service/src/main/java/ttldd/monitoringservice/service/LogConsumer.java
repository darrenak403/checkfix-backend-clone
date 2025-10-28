package ttldd.monitoringservice.service;

import lombok.extern.slf4j.Slf4j;
import ttldd.monitoringservice.dto.IamLogDTO;
import ttldd.monitoringservice.dto.ApiLogDTO;
import ttldd.monitoringservice.entity.EventLogEntity;
import ttldd.monitoringservice.entity.IamErrorLogEntity;
import ttldd.monitoringservice.entity.IamEventLogEntity;
import ttldd.monitoringservice.repo.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ttldd.monitoringservice.repo.IamErrorLogRepository;
import ttldd.monitoringservice.repo.IamLogRepository;
@Slf4j
@Service
@RequiredArgsConstructor
public class LogConsumer {

    private final EventLogRepository repository;
    private final IamLogRepository iamLogRepository;
    private final IamErrorLogRepository iamErrorLogRepository;


    @KafkaListener(topics = "api-logs", groupId = "monitoring-group",
            containerFactory = "apiLogKafkaListenerContainerFactory")
    public void consume(ApiLogDTO apiLog) {
        EventLogEntity entity = EventLogEntity.builder()
                .serviceName(apiLog.getServiceName())
                .description(String.format("%s %s -> %d (%d ms)",
                        apiLog.getMethod(), apiLog.getPath(), apiLog.getStatus(), apiLog.getLatency()))
                .status(apiLog.getStatus())
                .latency(apiLog.getLatency())
                .timestamp(apiLog.getTimestamp())
                .build();

        repository.save(entity);
        log.info("[Monitoring] Saved API log: {}", entity);
    }


    @KafkaListener(topics = "iam-logs", groupId = "monitoring-group",
            containerFactory = "iamLogKafkaListenerContainerFactory")

    public void consumeIamLogs(IamLogDTO message) {
        log.info("[Kafka] Received IAM log payload: {}", message);
        try {
            if ("FAILED".equalsIgnoreCase(message.getStatus()) || "ERROR".equalsIgnoreCase(message.getStatus())) {
                // Lưu log lỗi vào collection riêng
                IamErrorLogEntity errorEntity = IamErrorLogEntity.builder()
                        .serviceName(message.getServiceName())
                        .user(message.getUser())
                        .action(message.getAction())
                        .description(message.getDescription())
                        .exception(message.getException())
                        .stackTrace(message.getStackTrace())
                        .path(message.getPath())
                        .ip(message.getIp())
                        .timestamp(message.getTimestamp())
                        .build();

                iamErrorLogRepository.save(errorEntity);
                log.error(" [Monitoring] Saved IAM ERROR log: {}", errorEntity);
            } else {
                // Lưu log bình thường
                IamEventLogEntity entity = IamEventLogEntity.builder()
                        .serviceName(message.getServiceName())
                        .user(message.getUser())
                        .action(message.getAction())
                        .description(message.getDescription())
                        .status(message.getStatus())
                        .type(message.getType())
                        .method(message.getMethod())
                        .path(message.getPath())
                        .ip(message.getIp())
                        .timestamp(message.getTimestamp())
                        .build();

                iamLogRepository.save(entity);
                log.info("[Monitoring] Saved IAM SUCCESS log: {}", entity);
            }

        } catch (Exception e) {
            log.error("[Monitoring] Error while saving IAM log: {}", e.getMessage(), e);
        }
    }
}