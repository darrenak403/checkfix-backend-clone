package jungle.patientservice.service.imp;

import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.response.PatientResponse;
import jungle.patientservice.entity.Patient;
import jungle.patientservice.mapper.PatientMapper;
import jungle.patientservice.repo.PatientRepo;
import jungle.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImp implements PatientService {

    private final PatientRepo patientRepo;

    private final PatientMapper patientMapper;

    @Override
    public PatientResponse createPatient(PatientRequest patientDTO) {
        if (patientRepo.existsByEmail(patientDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + patientDTO.getEmail());
        }
        Patient patient = patientMapper.toPatientEntity(patientDTO);
        patientRepo.save(patient);
        return patientMapper.toPatientResponse(patient);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        List<Patient> patients = patientRepo.findAllByDeletedFalseOrderByIdDesc();
        return patients.stream().map(patientMapper::toPatientResponse).toList();
    }


    @Override
    public PatientResponse updatePatient(Long id, PatientRequest patientDTO) {
        Patient patient = patientRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        if (patientDTO.getUserId() != null) {
            patient.setUserId(patientDTO.getUserId());
        }

        if (StringUtils.hasText(patientDTO.getFullName())) {
            patient.setFullName(patientDTO.getFullName());
        }
        if (patientDTO.getYob() != null) {
            patient.setYob(patientDTO.getYob());
        }
        if (StringUtils.hasText(patientDTO.getGender())) {
            patient.setGender(patientDTO.getGender());
        }
        if (StringUtils.hasText(patientDTO.getAddress())) {
            patient.setAddress(patientDTO.getAddress());
        }
        if (StringUtils.hasText(patientDTO.getPhone())) {
            patient.setPhone(patientDTO.getPhone());
        }
        if (StringUtils.hasText(patientDTO.getEmail())) {
            patient.setEmail(patientDTO.getEmail());
        }
        if (patientDTO.getLastTestDate() != null) {
            patient.setLastTestDate(patientDTO.getLastTestDate());
        }
        if (StringUtils.hasText(patientDTO.getLastTestType())) {
            patient.setLastTestType(patientDTO.getLastTestType());
        }
        if (StringUtils.hasText(patientDTO.getInstrumentUsed())) {
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

    @Override
    public PatientResponse getPatient(Long id) {
        Patient patient = patientRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        return patientMapper.toPatientResponse(patient);
    }

    @Override
    public List<PatientResponse> getCurrentPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt principal = (Jwt) authentication.getPrincipal();
        Long userId = principal.getClaim("userId");
        List<Patient> patients = patientRepo.findAllByUserIdAndDeletedFalse(userId);
        if (patients.isEmpty()) {
            throw new IllegalArgumentException("No patient records found for userId: " + userId);
        }
        return patients.stream()
                .map(patientMapper::toPatientResponse)
                .toList();
    }

}
