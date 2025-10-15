package ttldd.labman.service;

import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.entity.Role;

import java.util.List;

public interface RoleService {
    void createRole(Role role);
    List<RoleResponse> getRoles();
}
