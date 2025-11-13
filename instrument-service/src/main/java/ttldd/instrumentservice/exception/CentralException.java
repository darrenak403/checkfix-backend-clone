package ttldd.instrumentservice.exception;


import ttldd.instrumentservice.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
//hello
@ControllerAdvice
public class CentralException {
//    @ExceptionHandler({DeleteException.class})
//    public ResponseEntity<?> centralLog(Exception e){
//        BaseResponse baseResponse = new BaseResponse();
//        baseResponse.setStatus(99);
//        baseResponse.setMessage(e.getMessage());
//        return ResponseEntity.ok(baseResponse);
//    }

    // Bắt lỗi validate @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        String combinedMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation failed");

        BaseResponse response = BaseResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(combinedMessage)
                .data(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleIllegalArgument(RuntimeException ex, HttpServletRequest request) {
        BaseResponse response = BaseResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Bắt tất cả exception chưa được xử lý riêng
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAllExceptions(Exception ex, HttpServletRequest request) {
//        BaseResponse response = BaseResponse.builder()
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(ex.getMessage())
//                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
//                .path(request.getRequestURI())
//                .timestamp(LocalDateTime.now())
//                .build();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
}
