package ttldd.labman.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttldd.labman.entity.Role;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.RoleService;

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
}
