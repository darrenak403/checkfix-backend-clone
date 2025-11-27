package ttldd.labman.service;

import ttldd.labman.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    List<String> getPermissionsByRoles(String roleCode);
    List<PermissionResponse> getAllPermissions();
}
