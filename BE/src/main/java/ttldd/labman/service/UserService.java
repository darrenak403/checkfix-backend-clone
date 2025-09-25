package ttldd.labman.service;

import ttldd.labman.dto.response.AuthResponse;
import ttldd.labman.dto.request.UserRequest;

import java.util.Map;

public interface UserService {
     void registerUser(UserRequest userDTO, String role);
    AuthResponse loginUser(UserRequest userDTO);
    String generateAuthorizationUri(String loginType);
    Map<String, Object> authenticateAndFetchProfile(String code, String loginType);
    AuthResponse loginOrSignup(Map<String, Object> userInfo, String role);
}
