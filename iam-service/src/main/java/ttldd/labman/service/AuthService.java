package ttldd.labman.service;

import org.springframework.stereotype.Service;
import ttldd.labman.dto.request.IntrospectRequest;
import ttldd.labman.dto.response.IntrospectResponse;

@Service
public interface AuthService {
    public IntrospectResponse introspect(IntrospectRequest request);
}
