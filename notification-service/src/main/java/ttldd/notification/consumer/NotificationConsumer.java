package ttldd.notification.consumer;




import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ttldd.event.dto.NotificationEvent;
import ttldd.notification.dto.request.Recipient;
import ttldd.notification.dto.request.SendEmailRequest;
import ttldd.notification.service.EmailService;
import ttldd.notification.service.EmailTemplateService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationConsumer {

    EmailService emailService;
    EmailTemplateService emailTemplateService;

    @KafkaListener(topics = "send-email")
    public void listenNotificationDelivery(NotificationEvent message){
        log.info("Message received: {}", message);
        message.setBody(emailTemplateService.renderTemplate(message.getTemplateCode(), message.getParam()));
        emailService.sendEmail(SendEmailRequest.builder()
                        .to(Recipient.builder()
                                .email(message.getRecipient())
                                .build())
                        .subject(message.getSubject())
                        .htmlContent(message.getBody())
                .build());
    }
}
