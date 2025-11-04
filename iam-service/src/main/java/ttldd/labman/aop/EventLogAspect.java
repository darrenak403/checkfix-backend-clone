package ttldd.labman.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;
import ttldd.labman.producer.EventLogProducer;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EventLogAspect {

    private final EventLogProducer eventLogProducer;

    @Around("execution(* ttldd.labman.service.imp.*.create*(..)) || " +
            "execution(* ttldd.labman.service.imp.*.update*(..)) || " +
            "execution(* ttldd.labman.service.imp.*.delete*(..))")
    public Object logCUDOperation(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String action = methodName.startsWith("create") ? "CREATE" :
                methodName.startsWith("update") ? "UPDATE" :
                        methodName.startsWith("delete") ? "DELETE" : "UNKNOWN";

        String entity = joinPoint.getTarget().getClass().getSimpleName().replace("ServiceImpl", "");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = (auth != null) ? auth.getName() : "SYSTEM";

        EventLogDTO logDTO = EventLogDTO.builder()
                .service("iam-service")
                .action(action)
                .entity(entity)
                .performedBy(actor)
                .timestamp(LocalDateTime.now())
                .traceId(UUID.randomUUID().toString()) // traceId giúp theo dõi log liên service
                .build();

        try {
            Object result = joinPoint.proceed();

            logDTO.setStatus("SUCCESS");
            logDTO.setMessage("✅ " + action + " executed successfully on " + entity);

            // Lấy entityId (nếu trong request có field id)
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                try {
                    var idField = args[0].getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    Object idValue = idField.get(args[0]);
                    if (idValue != null) logDTO.setEntityId(idValue.toString());
                } catch (Exception ignored) {}
            }

            eventLogProducer.sendEventLog(logDTO);
            return result;

        } catch (Exception e) {
            logDTO.setStatus("ERROR");
            logDTO.setMessage("❌ " + action + " failed on " + entity + ": " + e.getMessage());
            eventLogProducer.sendEventLog(logDTO);
            log.error("❌ Exception in {}: {}", methodName, e.getMessage());
            throw e;
        }
    }
}