package ttldd.warehouseservice.utils;

import ttldd.warehouseservice.repository.httpClient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final UserClient userClient;

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

    public String getFullName() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            var user = userClient.getUser(userId);
            if (user != null && user.getData() != null) {
                return user.getData().getFullName();
            }
        }
        return "Unknown";
    }

}
