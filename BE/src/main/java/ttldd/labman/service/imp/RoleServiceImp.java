package ttldd.labman.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttldd.labman.entity.Role;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.RoleService;

@Service
public class RoleServiceImp implements RoleService {
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void createRole(Role role) {
        roleRepo.save(role);
    }
}
