package ttldd.testorderservices.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;
import ttldd.testorderservices.producer.EventLogProducer;
import ttldd.testorderservices.util.JwtUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EventLogAspect {

    private final EventLogProducer eventLogProducer;
    private final JwtUtils jwtUtils;

    @Around("execution(* ttldd.testorderservices.service.imp.TestOrderService.create*(..)) || " +
            "execution(* ttldd.testorderservices.service.imp.TestOrderService.update*(..)) || " +
            "execution(* ttldd.testorderservices.service.imp.TestOrderService.softDelete(..))")
    public Object logCudOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();
        String action = detectAction(method);

        String actor = jwtUtils.getFullName() != null ? jwtUtils.getFullName() : "SYSTEM";
        String traceId = UUID.randomUUID().toString();
        Object result = null;
        String status = "SUCCESS";

        try {
            result = joinPoint.proceed();
            log.info("✅ {} executed successfully by {}", action, actor);
        } catch (Exception e) {
            status = "ERROR";
            log.error("❌ Error during {}: {}", action, e.getMessage());
            throw e;
        } finally {
            EventLogDTO logDTO = EventLogDTO.builder()
                    .service("testorder-service")
                    .action(action)
                    .entity("TestOrder")
                    .entityId(extractEntityId(joinPoint))
                    .performedBy(actor)
                    .status(status)
                    .message("✅ " + action + " executed successfully on TestOrder")
                    .timestamp(LocalDateTime.now())
                    .traceId(traceId)
                    .build();

            eventLogProducer.sendLog(logDTO);
        }

        return result;
    }

    private String detectAction(String methodName) {
        if (methodName.toLowerCase().contains("create")) return "CREATE";
        if (methodName.toLowerCase().contains("update")) return "UPDATE";
        if (methodName.toLowerCase().contains("delete")) return "DELETE";
        return "ACTION";
    }

    private String extractEntityId(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof Long id) {
                    return id.toString();
                }
            }
        } catch (Exception ignored) {}
        return "N/A";
    }
}