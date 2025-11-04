package ttldd.warehouseservice.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ttldd.event.dto.EventLogDTO;
import ttldd.warehouseservice.producer.EventLogProducer;
import ttldd.warehouseservice.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EventLogAspect {

    private final EventLogProducer eventLogProducer;
    private final JwtUtils jwtUtils;

    // Áp dụng cho tất cả các hàm CRUD trong package service.impl
    @Around("execution(* ttldd.warehouseservice.service.impl.*.create*(..)) || " +
            "execution(* ttldd.warehouseservice.service.impl.*.update*(..)) || " +
            "execution(* ttldd.warehouseservice.service.impl.*.delete*(..)) || " +
            "execution(* ttldd.warehouseservice.service.impl.*.change*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();

        // 🧩 Xác định hành động
        String action = method.startsWith("create") ? "CREATE" :
                method.startsWith("update") ? "UPDATE" :
                        method.startsWith("delete") ? "DELETE" :
                                method.startsWith("change") ? "CHANGE" : "ACTION";

        // 🧩 Xác định entity từ tên class
        String entity = joinPoint.getTarget().getClass().getSimpleName()
                .replace("ServiceImp", "")
                .replace("ServiceImpl", "");

        // 🧩 Lấy người thực hiện
        String performedBy = jwtUtils.getFullName();
        if (performedBy == null || performedBy.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            performedBy = (auth != null) ? auth.getName() : "SYSTEM";
        }


        EventLogDTO logDTO = EventLogDTO.builder()
                .service("warehouse-service")
                .action(action)
                .entity(entity)
                .performedBy(performedBy)
                .traceId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            // ⏩ Thực thi hàm chính
            Object result = joinPoint.proceed();

            logDTO.setStatus("SUCCESS");
            logDTO.setMessage("✅ " + action + " executed successfully on " + entity);

            // Thử lấy ID từ tham số nếu có
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                if (args[0] instanceof Number idNum) {
                    logDTO.setEntityId(String.valueOf(idNum));
                } else {
                    try {
                        var idField = args[0].getClass().getDeclaredField("id");
                        idField.setAccessible(true);
                        Object idValue = idField.get(args[0]);
                        if (idValue != null) logDTO.setEntityId(idValue.toString());
                    } catch (Exception ignored) {}
                }
            }

            // 📨 Gửi Kafka
            eventLogProducer.sendEventLog(logDTO);
            log.info("📤 Sent CRUD log: {}", logDTO);
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