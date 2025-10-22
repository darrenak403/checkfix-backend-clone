package ttldd.notification.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface EmailTemplateService {
    String renderTemplate(String templateCode, Map<String, Object> params);
}
