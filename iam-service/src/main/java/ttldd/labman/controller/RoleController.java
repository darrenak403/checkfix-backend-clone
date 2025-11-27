package ttldd.labman.controller;

import feign.Body;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.request.RoleRequest;
import ttldd.labman.dto.response.PermissionResponse;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.service.PermissionService;
import ttldd.labman.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('VIEW_ROLE')")
    public ResponseEntity<RestResponse<List<RoleResponse>>> getRoles() {
        List<RoleResponse> roles = roleService.getRoles();
        RestResponse<List<RoleResponse>> response = RestResponse.success(roles);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') and hasAnyAuthority('CREATE_ROLE')")
    public ResponseEntity<RestResponse<RoleResponse>> createRole(@RequestBody RoleRequest roleRequest) {
        RoleResponse role = roleService.createRole(roleRequest);
        RestResponse<RoleResponse> response = RestResponse.<RoleResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Role created successfully")
                .data(role)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') and hasAnyAuthority('UPDATE_ROLE')")
    public ResponseEntity<RestResponse<RoleResponse>> updateRole(@RequestParam Long id, @RequestBody RoleRequest request) {
        RoleResponse updatedRole = roleService.updateRole(id, request);
        RestResponse<RoleResponse> response = RestResponse.<RoleResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('VIEW_ROLE')")
    public ResponseEntity<RestResponse<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAllPermissions();
        RestResponse<List<PermissionResponse>> response = RestResponse.success(permissions);
        return ResponseEntity.ok(response);
    }
}
