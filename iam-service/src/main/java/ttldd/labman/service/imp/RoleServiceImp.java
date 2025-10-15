package ttldd.labman.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttldd.labman.dto.response.RoleResponse;
import ttldd.labman.entity.Role;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.RoleService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoleServiceImp implements RoleService {
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void createRole(Role role) {
        Optional<Role> existingRole = roleRepo.findByRoleCode(role.getRoleCode());
        if (existingRole.isEmpty()) {
            roleRepo.save(role);
            log.info("Role with code " + role.getRoleCode() + " created.");
        }else {
            log.info("Role with code " + role.getRoleCode() + " already exists.");
        }
    }

    @Override
    public List<RoleResponse> getRoles() {
        List<Role> role = roleRepo.findAll();
        return role.stream().map(r -> {
            RoleResponse response = new RoleResponse();
            response.setId(r.getId());
            response.setRoleCode(r.getRoleCode());
            response.setRoleName(r.getRoleName());
            return response;
        }).toList();
    }
}
