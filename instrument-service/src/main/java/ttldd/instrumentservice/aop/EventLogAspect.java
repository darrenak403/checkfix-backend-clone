package ttldd.instrumentservice.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;
import ttldd.instrumentservice.producer.EventLogProducer;
import ttldd.instrumentservice.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EventLogAspect {

    private final EventLogProducer eventLogProducer;
    private final JwtUtils jwtUtils;

    // 🔹 Áp dụng cho tất cả các service method có CRUD hoặc action chính
    @Around("execution(* ttldd.instrumentservice.service.imp.*.create*(..)) || " +
            "execution(* ttldd.instrumentservice.service.imp.*.update*(..)) || " +
            "execution(* ttldd.instrumentservice.service.imp.*.delete*(..)) || " +
            "execution(* ttldd.instrumentservice.service.imp.*.change*(..)) || " +
            "execution(* ttldd.instrumentservice.service.imp.*.install*(..)) || " +
            "execution(* ttldd.instrumentservice.service.imp.*.blood*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();

        // Xác định hành động
        String action = method.startsWith("create") ? "CREATE" :
                method.startsWith("update") ? "UPDATE" :
                        method.startsWith("delete") ? "DELETE" :
                                method.startsWith("install") ? "INSTALL" :
                                        method.startsWith("change") ? "CHANGE_MODE" :
                                                method.startsWith("blood") ? "ANALYZE_HL7" : "ACTION";

        String entity = joinPoint.getTarget().getClass().getSimpleName()
                .replace("ServiceImp", "")
                .replace("ServiceImpl", "");

        // Lấy người thực hiện
        String performedBy = jwtUtils.getFullName();
        if (performedBy == null || performedBy.isEmpty()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            performedBy = (auth != null) ? auth.getName() : "SYSTEM";
        }

        EventLogDTO logDTO = EventLogDTO.builder()
                .service("instrument-service")
                .action(action)
                .entity(entity)
                .performedBy(performedBy)
                .traceId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            Object result = joinPoint.proceed();

            logDTO.setStatus("SUCCESS");
            logDTO.setMessage("✅ " + action + " executed successfully on " + entity);

            // Thử lấy entityId từ request (nếu có)
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
            log.error("❌ Exception in {}: {}", method, e.getMessage());
            throw e;
        }
    }
}