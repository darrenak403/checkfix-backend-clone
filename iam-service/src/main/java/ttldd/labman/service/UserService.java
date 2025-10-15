package ttldd.labman.service;

import ttldd.labman.dto.request.UserCreationRequest;
import ttldd.labman.dto.request.UserUpdateRequest;
import ttldd.labman.dto.response.AuthResponse;
import ttldd.labman.dto.request.UserRequest;
import ttldd.labman.dto.response.UserResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
     void registerUser(UserRequest userDTO, String role);
    AuthResponse loginUser(UserRequest userDTO);
    String generateAuthorizationUri(String loginType);
    Map<String, Object> authenticateAndFetchProfile(String code, String loginType)throws IOException;
    AuthResponse loginOrSignup(Map<String, Object> userInfo, String role);
    AuthResponse refreshAccessToken(String refreshToken);
    List<UserResponse>  getAllUser();
    UserResponse getUserById(Long id);
    UserResponse createUser(UserCreationRequest userRequest);
    UserResponse updateUser(Long id, UserUpdateRequest userRequest);
}
