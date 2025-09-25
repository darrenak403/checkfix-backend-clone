package ttldd.labman.service;

import ttldd.labman.dto.request.AuthRequest;
import ttldd.labman.dto.request.UserRequest;

import java.util.Map;

public interface UserService {
    void registerUser(UserRequest userDTO, String role);
    AuthRequest loginUser(UserRequest userDTO);
    String generateAuthorizationUri(String loginType);
    Map<String, Object> authenticateAndFetchProfile(String code, String loginType);
    AuthRequest loginOrSignup(Map<String, Object> userInfo, String role);
}
