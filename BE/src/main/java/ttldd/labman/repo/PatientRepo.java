package ttldd.labman.repo;

import ttldd.labman.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    List<Patient> findAllByDeletedFalse();
    Optional<Patient> findByIdAndDeletedFalse(long id);
}
