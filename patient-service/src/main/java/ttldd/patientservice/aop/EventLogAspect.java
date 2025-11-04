package ttldd.patientservice.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;
import ttldd.patientservice.producer.EventLogProducer;

import java.time.LocalDateTime;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EventLogAspect {

    private final EventLogProducer eventLogProducer;

    @Around("execution(* ttldd.patientservice.service.imp.*.create*(..)) || " +
            "execution(* ttldd.patientservice.service.imp.*.update*(..)) || " +
            "execution(* ttldd.patientservice.service.imp.*.delete*(..))")
    public Object logCUDOperation(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().getName();
        String action =
                method.startsWith("create") ? "CREATE" :
                        method.startsWith("update") ? "UPDATE" :
                                method.startsWith("delete") ? "DELETE" : "UNKNOWN";

        String entity = joinPoint.getTarget().getClass().getSimpleName().replace("ServiceImp", "");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = (auth != null) ? auth.getName() : "SYSTEM";

        EventLogDTO logDTO = EventLogDTO.builder()
                .service("patient-service")
                .action(action)
                .entity(entity)
                .performedBy(actor)
                .traceId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            Object result = joinPoint.proceed();

            logDTO.setStatus("SUCCESS");
            logDTO.setMessage("✅ " + action + " executed successfully on " + entity);

            // Nếu có field id trong request, lấy làm entityId
            for (Object arg : joinPoint.getArgs()) {
                try {
                    var idField = arg.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    Object idValue = idField.get(arg);
                    if (idValue != null) {
                        logDTO.setEntityId(idValue.toString());
                    }
                } catch (NoSuchFieldException ignored) {}
            }

            eventLogProducer.sendEventLog(logDTO);
            return result;

        } catch (Exception e) {
            logDTO.setStatus("ERROR");
            logDTO.setMessage("❌ " + action + " failed on " + entity + ": " + e.getMessage());
            eventLogProducer.sendEventLog(logDTO);
            throw e;
        }
    }
}