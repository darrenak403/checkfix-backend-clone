package ttldd.labman.service;

import ttldd.labman.dto.request.RoleRequest;
import ttldd.labman.dto.request.RoleUpdateRequest;
import ttldd.labman.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest role);
    List<RoleResponse> getRoles();
    RoleResponse updateRole(Long id, RoleUpdateRequest request);
    void deleteRole(Long id);
}
