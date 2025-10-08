package jungle.patientservice.repo;



import jungle.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    List<Patient> findAllByDeletedFalseOrderByIdDesc();
    Optional<Patient> findByIdAndDeletedFalse(long id);
    List<Patient> findAllByUserIdAndDeletedFalse(Long userId);
    boolean existsByPatientCode(String patientCode);
    Optional<Patient> findFirstByUserIdAndDeletedFalse(Long userId);
}
