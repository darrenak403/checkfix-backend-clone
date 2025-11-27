package ttldd.notification.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ttldd.notification.service.EmailTemplateService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailTemplateServiceImpl implements EmailTemplateService {
    TemplateEngine templateEngine;

    public String renderTemplate(String templateCode, Map<String, Object> params) {
        Context context = new Context();
        if (params != null) {
            context.setVariables(params);
        }

        String templateName;
        switch (templateCode) {
            case "WELCOME_EMAIL":
                templateName = "email/welcome-email";
                break;
            case "FORGOT_PASSWORD_EMAIL":
                templateName = "email/otp-template";
                break;
            case "RESET_PASSWORD_EMAIL":
                templateName = "email/password-changed-template";
                break;

            case "ACCOUNT_CREDENTIAL_EMAIL":
                templateName = "email/account-credential";
                break;
            case "TEST_RESULT_NOTIFICATION":
                templateName = "email/test-result-template";
                break;
            default:
                throw new IllegalArgumentException("Unknown template code: " + templateCode);
        }

        return templateEngine.process(templateName, context);
    }
}
