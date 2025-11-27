package ttldd.labman.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ttldd.labman.dto.request.PermissionRequest;
import ttldd.labman.dto.response.PermissionResponse;
import ttldd.labman.entity.Permission;
import ttldd.labman.entity.Role;
import ttldd.labman.mapper.RoleMapper;
import ttldd.labman.repo.PermissionRepo;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.PermissionService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final RoleRepo roleRepository;
    private final PermissionRepo permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @Cacheable(value = "role_permissions", key = "#roleCode")
    public List<String> getPermissionsByRoles(String roleCode) {
        return roleRepository.findPermissionsByRoleCode(roleCode);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAllByDeletedFalse();

        return permissions.stream().map(
                roleMapper::toPermissionResponse
        ).toList();
    }

    @Override
    public PermissionResponse createPermission(PermissionRequest permissionRequest) {

        if (permissionRepository.existsByName(permissionRequest.getName())) {
            throw new IllegalArgumentException(
                    "Permission đã có trong hệ thống: " + permissionRequest.getName()
            );
        }
        Permission permission = roleMapper.toPermissionEntity(permissionRequest);
        permissionRepository.save(permission);
        return roleMapper.toPermissionResponse(permission);
    }

    @Override
    public void deletePermission(Long permissionId) {
        Permission permission = permissionRepository.findByIdAndDeletedFalse(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        List<Role> roles = new ArrayList<>(permission.getRoles());

        for (Role role : roles) {
            permission.removeRole(role);
        }
        permission.setDeleted(true);
        permissionRepository.save(permission);
    }


}
