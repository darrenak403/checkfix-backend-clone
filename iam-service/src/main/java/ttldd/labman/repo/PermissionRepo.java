package ttldd.labman.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttldd.labman.entity.Permission;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {

}
