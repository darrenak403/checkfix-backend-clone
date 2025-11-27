package ttldd.labman.mapper;

import org.mapstruct.Mapper;
import ttldd.labman.dto.response.PermissionResponse;
import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.entity.Permission;
import ttldd.labman.entity.Role;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
    PermissionResponse toPermissionResponse(Permission permission);
    List<RoleResponse> toRoleResponseList(List<Role> roles);
}
