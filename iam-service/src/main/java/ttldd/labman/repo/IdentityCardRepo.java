package ttldd.labman.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttldd.labman.entity.IdentityCard;

import java.util.Optional;

@Repository
public interface IdentityCardRepo extends JpaRepository<IdentityCard, Long> {
    Optional<IdentityCard> findByIdentityNumber(String identityNumber);

    IdentityCard findByUserId(Long userId);
}
