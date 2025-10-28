package ttldd.notification.service;



import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ttldd.notification.dto.request.EmailRequest;
import ttldd.notification.dto.request.SendEmailRequest;
import ttldd.notification.dto.request.Sender;
import ttldd.notification.dto.response.EmailResponse;
import ttldd.notification.repository.httpclient.EmailClient;

import java.util.List;

@Service
public interface EmailService {
    EmailResponse sendEmail(SendEmailRequest request);
}
