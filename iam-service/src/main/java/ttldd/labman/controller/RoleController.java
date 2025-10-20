package ttldd.labman.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<RestResponse<List<RoleResponse>>> getRoles() {
        List<RoleResponse> roles = roleService.getRoles();
        RestResponse<List<RoleResponse>> response = RestResponse.success(roles);
        return ResponseEntity.ok(response);
    }
}
