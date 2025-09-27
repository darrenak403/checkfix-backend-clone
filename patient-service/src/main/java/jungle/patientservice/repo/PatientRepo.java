package jungle.patientservice.repo;



import jungle.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    List<Patient> findAllByDeletedFalse();
    Optional<Patient> findByIdAndDeletedFalse(long id);
}
