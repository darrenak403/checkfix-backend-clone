package ttldd.labman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.request.UserCreationRequest;
import ttldd.labman.dto.request.UserRequest;
import ttldd.labman.dto.response.BaseResponse;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.dto.response.UserResponse;
import ttldd.labman.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR')  or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        BaseResponse response = new BaseResponse();
        List<UserResponse> users = userService.getAllUser();
        response.setStatus(200);
        response.setData(users);
        response.setMessage("Fetched all users successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR')  or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        RestResponse<UserResponse> response = RestResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Fetched user successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public  ResponseEntity<RestResponse<UserResponse>> createUser(@RequestBody UserCreationRequest user) {
        UserResponse createdUser = userService.createUser(user);
        RestResponse<UserResponse> response = RestResponse.<UserResponse>builder()
                .statusCode(201)
                .message("User created successfully")
                .data(createdUser)
                .build();
        return ResponseEntity.status(201).body(response);
    }
}
