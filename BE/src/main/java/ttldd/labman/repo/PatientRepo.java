package ttldd.labman.repo;

import ttldd.labman.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    List<Patient> findAllByDeletedFalse();
}
