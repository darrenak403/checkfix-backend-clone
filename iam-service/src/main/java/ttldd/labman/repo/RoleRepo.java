package ttldd.labman.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ttldd.labman.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCode(String roleCode);

    @Query("SELECT p.name FROM Role r JOIN r.permissions p WHERE r.roleCode = :roleCode and p.deleted = false")
    List<String> findPermissionsByRoleCode(@Param("roleCode") String roleCode);
}
