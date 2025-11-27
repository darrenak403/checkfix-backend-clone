package ttldd.labman.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ttldd.labman.dto.response.PermissionResponse;
import ttldd.labman.entity.Permission;
import ttldd.labman.mapper.RoleMapper;
import ttldd.labman.repo.PermissionRepo;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.PermissionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final RoleRepo roleRepository;
    private final PermissionRepo permissionRepository;
    private  final RoleMapper roleMapper;

    @Override
    @Cacheable(value = "role_permissions", key = "#roleCode")
    public List<String> getPermissionsByRoles(String roleCode) {
        return roleRepository.findPermissionsByRoleCode(roleCode);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();

        return permissions.stream().map(
                roleMapper::toPermissionResponse
        ).toList();
    }


}
