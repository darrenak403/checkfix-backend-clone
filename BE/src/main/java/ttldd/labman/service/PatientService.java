package ttldd.labman.service;

import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;
import ttldd.labman.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttldd.labman.repo.PatientRepo;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepo patientRepo;

    public PatientResponse createPatient(PatientRequest patientDTO) {
        Patient patient = Patient.builder()
                .name(patientDTO.getFullName())
                .yob(patientDTO.getYearOfBirth())
                .gender(patientDTO.getGender())
                .address(patientDTO.getAddress())
                .phone(patientDTO.getPhone())
                .email(patientDTO.getEmail())
                .lastTestDate(patientDTO.getLastTestDate())
                .lastTestType(patientDTO.getLastTestType())
                .instrumentUsed(patientDTO.getInstrumentUsed())
                .build();
        patientRepo.save(patient);
        return mapToResponse(patient);
    }

    private PatientResponse mapToResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setName(patient.getName());
        response.setYob(patient.getYob());
        response.setGender(patient.getGender());
        response.setAddress(patient.getAddress());
        response.setPhone(patient.getPhone());
        response.setEmail(patient.getEmail());
        response.setLastTestDate(patient.getLastTestDate());
        response.setLastTestType(patient.getLastTestType());
        response.setInstrumentUsed(patient.getInstrumentUsed());
        return response;
    }


}
