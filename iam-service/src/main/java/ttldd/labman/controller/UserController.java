package ttldd.labman.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttldd.labman.dto.request.UserCardRequest;
import ttldd.labman.dto.response.UserCardResponse;
import ttldd.labman.dto.request.UpdateAvatarRequest;
import ttldd.labman.dto.request.UserCreationRequest;
import ttldd.labman.dto.request.UserUpdateRequest;
import ttldd.labman.dto.response.BaseResponse;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.dto.response.UserResponse;
import ttldd.labman.service.IdentityCardService;
import ttldd.labman.service.UserService;
import ttldd.labman.service.VnptKycService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final VnptKycService vnptKycService;

    private final IdentityCardService identityCardService;

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

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<UserResponse>> updateUser(@PathVariable Long id ,@Valid @RequestBody UserUpdateRequest user) {
        UserResponse updatedUser = userService.updateUser(id,user);
        RestResponse<UserResponse> response = RestResponse.<UserResponse>builder()
                .statusCode(200)
                .message("User updated successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<UserResponse>> updateAvatar(@RequestBody UpdateAvatarRequest rq) {
        UserResponse updatedUser = userService.updateAvatar(rq);
        RestResponse<UserResponse> response = RestResponse.<UserResponse>builder()
                .statusCode(200)
                .message("User avatar updated successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/extract-id-card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestResponse<UserCardResponse>> extractIdCard(@RequestParam("frontImage") MultipartFile frontImage,
                                                                        @RequestParam("backImage") MultipartFile backImage) {
        UserCardResponse userResponse = vnptKycService.extractIdCardInfo(frontImage, backImage);
        RestResponse<UserCardResponse> response = RestResponse.<UserCardResponse>builder()
                .statusCode(200)
                .message("ID card extracted successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/identity")
    public ResponseEntity<RestResponse<UserCardResponse>> saveIdentityCard(@RequestBody UserCardRequest rq) {
        UserCardResponse savedCard = identityCardService.saveIdentityCard(rq);
        RestResponse<UserCardResponse> response = RestResponse.<UserCardResponse>builder()
                .statusCode(200)
                .message("ID card saved successfully")
                .data(savedCard)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/identity")
    public ResponseEntity<RestResponse<UserCardResponse>> getIdentityCard() {
        UserCardResponse card = identityCardService.getIdentityCardByUserId();
        RestResponse<UserCardResponse> response = RestResponse.<UserCardResponse>builder()
                .statusCode(200)
                .message("ID card retrieved successfully")
                .data(card)
                .build();
        return ResponseEntity.ok(response);
    }

}
