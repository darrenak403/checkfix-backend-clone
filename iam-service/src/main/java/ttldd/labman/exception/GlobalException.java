package ttldd.labman.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ttldd.labman.dto.LogEventDTO;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.dto.response.BaseResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public GlobalException(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<?>> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("RuntimeException: {}", ex.getMessage());
        sendErrorLog(ex, request, "RUNTIME_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse<?>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        log.error("BadCredentialsException: {}", ex.getMessage());
        sendErrorLog(ex, request, "BAD_CREDENTIALS");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RestResponse.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Tên đăng nhập hoặc mật khẩu không chính xác"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errorMessages = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        String combinedMessage = errorMessages.size() > 1
                ? String.join(", ", errorMessages)
                : errorMessages.getFirst();

        sendErrorLog(ex, request, "VALIDATION_ERROR");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        combinedMessage));
    }

    @ExceptionHandler({InsertException.class})
    public ResponseEntity<?> centralLog(Exception e, HttpServletRequest request){
        sendErrorLog(e, request, "INSERT_ERROR");
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(99);
        baseResponse.setMessage(e.getMessage());
        return ResponseEntity.ok(baseResponse);
    }

    private void sendErrorLog(Exception ex, HttpServletRequest request, String action) {
        try {
            LogEventDTO logEvent = LogEventDTO.builder()
                    .serviceName("iam-service")
                    .user(request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous")
                    .action(action)
                    .description(ex.getMessage())
                    .status("FAILED")
                    .type("ERROR")
                    .method(request.getMethod())
                    .path(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .requestId(UUID.randomUUID().toString())
                    .exception(ex.getClass().getSimpleName())
                    .stackTrace(getStackTrace(ex))
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("iam-logs", logEvent);
            log.error("❌ Sent ERROR log to Kafka from GlobalException: {}", logEvent);
        } catch (Exception e) {
            log.error("⚠️ Failed to send error log from GlobalException: {}", e.getMessage());
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

