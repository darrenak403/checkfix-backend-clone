package ttldd.labman.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ttldd.labman.dto.LogEventDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final HttpServletRequest request;

    // Bắt tất cả các phương thức trong controller
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        sendLog(joinPoint, null, result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        sendLog(joinPoint, ex, null);
    }

    private void sendLog(JoinPoint joinPoint, Exception ex, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            // Tên user
            String username = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
            String service = "iam-service"; // có thể dynamic hóa bằng @Value("${spring.application.name}")
            String methodName = signature.getMethod().getName();

            // Thông tin request
            String httpMethod = request.getMethod();
            String path = request.getRequestURI();
            String ip = request.getRemoteAddr();
            String params = Arrays.toString(joinPoint.getArgs());

            // Tạo log DTO
            LogEventDTO logEvent = LogEventDTO.builder()
                    .serviceName(service)
                    .user(username)
                    .action(methodName.toUpperCase())
                    .description("Auto log for " + methodName)
                    .status(ex == null ? "SUCCESS" : "FAILED")
                    .type(ex == null ? "INFO" : "ERROR")
                    .method(httpMethod)
                    .path(path)
                    .ip(ip)
                    .requestId(UUID.randomUUID().toString())
                    .exception(ex != null ? ex.getClass().getSimpleName() : null)
                    .stackTrace(ex != null ? getStackTrace(ex) : null)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Gửi log
            kafkaTemplate.send("iam-logs", logEvent);

            if (ex == null)
                log.info("✅ Sent SUCCESS log to Kafka: {}", logEvent);
            else
                log.error("❌ Sent ERROR log to Kafka: {}", logEvent);
        } catch (Exception e) {
            log.error("⚠️ Failed to send log: {}", e.getMessage());
        }
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : ex.getStackTrace()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}