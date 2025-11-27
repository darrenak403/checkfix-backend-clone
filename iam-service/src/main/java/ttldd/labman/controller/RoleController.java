package ttldd.labman.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.request.PermissionRequest;
import ttldd.labman.dto.request.RoleRequest;
import ttldd.labman.dto.request.RoleUpdateRequest;
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
    public ResponseEntity<RestResponse<RoleResponse>> updateRole(@RequestParam Long id, @RequestBody RoleUpdateRequest request) {
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

    @DeleteMapping("/permissions/{permissionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') and hasAnyAuthority('DELETE_ROLE')")
    public ResponseEntity<RestResponse<Void>> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        RestResponse<Void> response = RestResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Permission deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') and hasAnyAuthority('CREATE_ROLE')")
    public ResponseEntity<RestResponse<PermissionResponse>> createPermission(@RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permission = permissionService.createPermission(permissionRequest);
        RestResponse<PermissionResponse> response = RestResponse.<PermissionResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Permission created successfully")
                .data(permission)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
