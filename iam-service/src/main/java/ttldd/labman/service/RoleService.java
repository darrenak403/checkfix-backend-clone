package ttldd.labman.service;

import ttldd.labman.dto.request.RoleRequest;
import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.entity.Role;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest role);
    List<RoleResponse> getRoles();
    RoleResponse updateRole(Long id, RoleRequest request);
    void deleteRole(Long id);
}
