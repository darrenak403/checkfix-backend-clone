package ttldd.labman.service.imp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttldd.labman.dto.request.RoleRequest;
import ttldd.labman.dto.request.RoleUpdateRequest;
import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.entity.Permission;
import ttldd.labman.entity.Role;
import ttldd.labman.mapper.RoleMapper;
import ttldd.labman.repo.PermissionRepo;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.RoleService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImp implements RoleService {
     RoleRepo roleRepo;
     PermissionRepo permissionRepo;
     RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepo.findByRoleCode(request.getRoleCode()).isPresent()) {
            throw new IllegalArgumentException("Role code " + request.getRoleCode() + " already exists.");
        }

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepo.findAllById(request.getPermissionIds());
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepo.save(role);
        return roleMapper.toRoleResponse(savedRole);
    }

    @Override
    public List<RoleResponse> getRoles() {
        List<Role> role = roleRepo.findAll();
        return roleMapper.toRoleResponseList(role);
    }

    @Override
    public RoleResponse updateRole(Long roleId, RoleUpdateRequest request) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));


        if (request.getPermissionIds() != null) {

            List<Permission> newPermissions =
                    permissionRepo.findAllByIdInAndDeletedFalse(request.getPermissionIds());

            if (newPermissions.size() != request.getPermissionIds().size()) {
                throw new IllegalArgumentException("Some permissions not found");
            }

            for (Permission p : newPermissions) {
                if (!role.getPermissions().contains(p)) {
                    role.getPermissions().add(p);
                }
            }
        }

        roleRepo.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        if (!role.getUser().isEmpty()) {
            throw new RuntimeException("Không thể xóa Role này vì đang có " + role.getUser().size() + " tài khoản sử dụng. Vui lòng gỡ quyền các tài khoản này trước.");
        }
    }


}
