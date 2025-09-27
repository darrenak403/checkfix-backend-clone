package ttldd.labman.repo;

import ttldd.labman.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
<<<<<<< HEAD
import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    List<Patient> findAllByDeletedFalse();
    Optional<Patient> findByIdAndDeletedFalse(long id);
=======

public interface PatientRepo extends JpaRepository<Patient, Long> {
    List<Patient> findAllByDeletedFalse();
>>>>>>> a46baebf7f0416d41011ef25bb7d5a36256d5562
}
