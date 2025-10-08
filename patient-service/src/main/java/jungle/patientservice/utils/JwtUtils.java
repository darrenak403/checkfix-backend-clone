package jungle.patientservice.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    public Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = jwtAuth.getToken();
            Object idClaim = jwt.getClaim("userId");
            if (idClaim != null) {
                return Long.parseLong(idClaim.toString());
            }
        }
        return null;
    }
}
