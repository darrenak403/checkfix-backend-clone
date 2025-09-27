package ttldd.labman.service.imp;

import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;
import ttldd.labman.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttldd.labman.mapper.PatientMapper;
import ttldd.labman.repo.PatientRepo;
import ttldd.labman.service.PatientService;

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
        Patient patient = patientRepo.findById(id)
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




}
