// Add new REST controller that exposes GET /api/monitorings
package ttldd.monitoringservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ttldd.event.dto.EventLogDTO;
import ttldd.monitoringservice.entity.EventLog;
import ttldd.monitoringservice.service.EventLogService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monitorings")
@RequiredArgsConstructor
public class MonitoringController {

    private final EventLogService eventLogService;

    // GET /monitoring/api/monitorings - Lấy tất cả event logs
    @GetMapping
    public List<EventLogDTO> getAll() {
        List<EventLog> logs = eventLogService.getAllLogs();
        return logs.stream().map(this::toDto).collect(Collectors.toList());
    }

    private EventLogDTO toDto(EventLog e) {
        return EventLogDTO.builder()
                .service(e.getService())
                .action(e.getAction())
                .entity(e.getEntity())
                .entityId(e.getEntityId())
                .performedBy(e.getPerformedBy())
                .status(e.getStatus())
                .message(e.getMessage())
                .traceId(e.getTraceId())
                .timestamp(e.getTimestamp())
                .build();
    }
}
