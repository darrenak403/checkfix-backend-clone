package ttldd.labman.service;

import ttldd.labman.dto.UserDTO;
import ttldd.labman.entity.User;

import java.util.Map;

public interface UserService {
    void registerUser(UserDTO userDTO, String role);
    String loginUser(UserDTO userDTO);
    String generateAuthorizationUri(String loginType);
    Map<String, Object> authenticateAndFetchProfile(String code, String loginType);
    String loginOrSignup(Map<String, Object> userInfo, String role);
}
