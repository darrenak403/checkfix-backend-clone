package ttldd.labman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ttldd.labman.dto.response.BaseResponse;
import ttldd.labman.dto.response.UserResponse;
import ttldd.labman.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR') ")
    public ResponseEntity<?> getAllUsers() {
        BaseResponse response = new BaseResponse();
        List<UserResponse> users = userService.getAllUser();
        response.setStatus(200);
        response.setData(users);
        response.setMessage("Fetched all users successfully");
        return ResponseEntity.ok(response);
    }
}
