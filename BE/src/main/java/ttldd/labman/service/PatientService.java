package ttldd.labman.service;

import ttldd.labman.dto.PatientDTO;
import ttldd.labman.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttldd.labman.repo.PatientRepo;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepo patientRepo;


    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = Patient.builder()
                .name(patientDTO.getFullName())
                .yob(patientDTO.getYearOfBirth())
                .visitDate(patientDTO.getDateOfVisit())
                .build();
        patientRepo.save(patient);
        return patientDTO;
    }

}
