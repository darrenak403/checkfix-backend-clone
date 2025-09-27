package jungle.patientservice.service.imp;

import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.response.PatientResponse;
import jungle.patientservice.entity.Patient;
import jungle.patientservice.mapper.PatientMapper;
import jungle.patientservice.repo.PatientRepo;
import jungle.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImp implements PatientService {

    private final PatientRepo patientRepo;

    private final PatientMapper patientMapper;

    @Override
    public PatientResponse createPatient(PatientRequest patientDTO) {
        Patient patient = patientMapper.toPatientEntity(patientDTO);
        patientRepo.save(patient);
        return patientMapper.toPatientResponse(patient);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        List<Patient> patients = patientRepo.findAllByDeletedFalse();
        return patients.stream().map(patientMapper::toPatientResponse).toList();
    }


    @Override
    public PatientResponse updatePatient(Long id, PatientRequest patientDTO) {
        Patient patient = patientRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        if (patientDTO.getFullName() != null) {
            patient.setName(patientDTO.getFullName());
        }
        if (patientDTO.getYearOfBirth() != null) {
            patient.setYob(patientDTO.getYearOfBirth());
        }
        if (patientDTO.getGender() != null) {
            patient.setGender(patientDTO.getGender());
        }
        if (patientDTO.getAddress() != null) {
            patient.setAddress(patientDTO.getAddress());
        }
        if (patientDTO.getPhone() != null) {
            patient.setPhone(patientDTO.getPhone());
        }
        if (patientDTO.getEmail() != null) {
            patient.setEmail(patientDTO.getEmail());
        }
        if (patientDTO.getLastTestDate() != null) {
            patient.setLastTestDate(patientDTO.getLastTestDate());
        }
        if (patientDTO.getLastTestType() != null) {
            patient.setLastTestType(patientDTO.getLastTestType());
        }
        if (patientDTO.getInstrumentUsed() != null) {
            patient.setInstrumentUsed(patientDTO.getInstrumentUsed());
        }
        patientRepo.save(patient);
        return patientMapper.toPatientResponse(patient);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patient.setDeleted(true);
        patientRepo.save(patient);
    }


}
