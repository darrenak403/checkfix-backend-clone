package ttldd.labman.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.request.AuthRequest;
import ttldd.labman.dto.request.UserRequest;
import ttldd.labman.response.BaseResponse;
import ttldd.labman.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String MANAGER_ROLE = "ROLE_MANAGER";
    private static final String SERVICE_ROLE = "ROLE_SERVICE";
    private static final String LAB_USER_ROLE = "ROLE_LAB_USER";
    private static final String PATIENT_ROLE = "ROLE_PATIENT";

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserRequest userDTO) {

        userService.registerUser(userDTO, PATIENT_ROLE);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(HttpStatus.CREATED.value());
        baseResponse.setMessage("Register user successfully");
        baseResponse.setData(null);
        return ResponseEntity.ok(baseResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAccount(@Valid @RequestBody UserRequest userDTO) {
        AuthRequest token = userService.loginUser(userDTO);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Login successfully");
        baseResponse.setData(token);
        return ResponseEntity.ok(baseResponse);
    }

    @GetMapping("/google/social")
    public ResponseEntity<?> getAuthorizationUri(@RequestParam String loginType) {
        BaseResponse baseResponse = new BaseResponse();
        String data = userService.generateAuthorizationUri(loginType);
        if(data == null || data.isEmpty()){
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Failed to generate authorization uri");
            return ResponseEntity.badRequest().body(baseResponse);
        }
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(data);
        return ResponseEntity.ok(baseResponse);
    }

    @GetMapping("/google/social/callback")
    public ResponseEntity<?> authenticateAndFetchProfile(@RequestParam String code, @RequestParam String loginType) {
        BaseResponse response = new BaseResponse();
        Map<String, Object> data = userService.authenticateAndFetchProfile(code, loginType);
        if(data == null || data.isEmpty()){
            response.setMessage("Failed to authenticate and fetch profile");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }

        // RegisterOrLogin Oauth2 Google
        AuthRequest token = userService.loginOrSignup(data, "ROLE_PATIENT");
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Login successfully");
        response.setData(token);
        return ResponseEntity.ok(response);
    }
}
