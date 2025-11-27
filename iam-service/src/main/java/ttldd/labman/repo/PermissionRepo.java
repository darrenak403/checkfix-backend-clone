package ttldd.labman.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttldd.labman.entity.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {
    boolean existsByName(String name);
    Optional<Permission> findByIdAndDeletedFalse(Long id);
    List<Permission> findAllByIdInAndDeletedFalse(List<Long> ids);
    List<Permission> findAllByDeletedFalse();
}
