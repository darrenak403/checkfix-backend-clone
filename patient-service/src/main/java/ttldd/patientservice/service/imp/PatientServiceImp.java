package ttldd.patientservice.service.imp;

import org.springframework.kafka.core.KafkaTemplate;
import ttldd.event.dto.PatientUpdateEvent;
import ttldd.event.dto.UserUpdatedEvent;
import ttldd.patientservice.dto.request.PatientRequest;
import ttldd.patientservice.dto.request.PatientUpdateRequest;
import ttldd.patientservice.dto.response.PageResponse;
import ttldd.patientservice.dto.response.PatientResponse;
import ttldd.patientservice.dto.response.RestResponse;
import ttldd.patientservice.dto.response.UserResponse;
import ttldd.patientservice.entity.Patient;
import ttldd.patientservice.mapper.PatientMapper;
import ttldd.patientservice.repo.PatientRepo;
import ttldd.patientservice.repo.httpClient.UserClient;
import ttldd.patientservice.service.PatientService;
import ttldd.patientservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImp implements PatientService {

    private final PatientRepo patientRepo;

    private final PatientMapper patientMapper;

    private final JwtUtils jwtUtils;

    private final UserClient userClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public PatientResponse createPatient(PatientRequest patientDTO) {
        RestResponse<UserResponse> user = userClient.getUser(patientDTO.getUserId());
        if (user.getData() == null) {
            throw new IllegalArgumentException("UserId not found: " + patientDTO.getUserId());
        }
        boolean exists = patientRepo.existsByUserIdAndDeletedFalse(patientDTO.getUserId());
        if (exists) {
            throw new IllegalStateException("Bệnh nhân đã có hồ sơ trong hệ thống");
        }
        Patient patient = patientMapper.toPatientEntity(patientDTO);
        String patientCode = generatePatientCode();

        patient.setPatientCode(patientCode);
        patient.setFullName(user.getData().getFullName());
        patient.setEmail(user.getData().getEmail());
        patient.setPhone(user.getData().getPhone());
        patient.setGender(user.getData().getGender());
        patient.setAddress(user.getData().getAddress());
        patient.setYob(user.getData().getDateOfBirth());
        patient.setCreatedBy(jwtUtils.getFullName());
        patient.setCreatedAt(LocalDateTime.now());
        patient.setAvatarUrl(user.getData().getAvatarUrl());

        patientRepo.save(patient);
        return patientMapper.toPatientResponse(patient);
    }


    private String generatePatientCode(){
        String code;
        do {
            String shortUUID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            code = "PAT-2025-" + shortUUID;
        } while (patientRepo.existsByPatientCode(code));
        return code;
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        List<Patient> patients = patientRepo.findAllByDeletedFalseOrderByIdDesc();
        return patients.stream().map(patientMapper::toPatientResponse).toList();
    }


    @Override
    public PatientResponse updatePatient(Long id, PatientUpdateRequest patientDTO) {
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
        patient.setModifiedBy(jwtUtils.getFullName());
        patientRepo.save(patient);
        PatientUpdateEvent event = PatientUpdateEvent.builder()
                .id(patient.getUserId())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .avatarUrl(patient.getAvatarUrl())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getYob())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .userId(patient.getUserId())
                .build();
        kafkaTemplate.send("patient-updated", event);
        log.info("Sent Kafka event: {}", event);
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
        List<Patient> patients = patientRepo.findTop1ByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId);
        if (patients.isEmpty()) {
            throw new IllegalArgumentException("No patient records found for userId: " + userId);
        }
        return patients.stream()
                .map(patientMapper::toPatientResponse)
                .toList();
    }

    @Override
    public PageResponse<PatientResponse> getPatients(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var patientPage = patientRepo.findAllByDeletedFalse(pageable);

        return PageResponse.<PatientResponse>builder()
                .data(patientPage.getContent().stream().map(patientMapper::toPatientResponse).toList())
                .currentPage(page)
                .totalItems(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .pageSize(size)
                .build();
    }

    @Override
    public void asyncPatientFromUser(UserUpdatedEvent event) {
        Patient patient = patientRepo.findByUserIdAndDeletedFalse(event.getId());
            if (StringUtils.hasText(event.getFullName())) {
                patient.setFullName(event.getFullName());
            }
            if (StringUtils.hasText(event.getEmail())) {
                patient.setEmail(event.getEmail());
            }
            if (StringUtils.hasText(event.getPhone())) {
                patient.setPhone(event.getPhone());
            }
            if (StringUtils.hasText(event.getGender())) {
                patient.setGender(event.getGender());
            }
            if (StringUtils.hasText(event.getAvatarUrl())) {
                patient.setAvatarUrl(event.getAvatarUrl());
            }
            if (event.getDateOfBirth() != null) {
                patient.setYob(event.getDateOfBirth());
            }
            if (StringUtils.hasText(event.getAddress())) {
                patient.setAddress(event.getAddress());
            }
            patientRepo.save(patient);
        PatientUpdateEvent updateEvent = PatientUpdateEvent.builder()
                .id(patient.getId())
                .userId(event.getId())
                .fullName(event.getFullName())
                .phone(event.getPhone())
                .email(event.getEmail())
                .dateOfBirth(event.getDateOfBirth())
                .address(event.getAddress())
                .gender(event.getGender())
                .avatarUrl(event.getAvatarUrl())
                .build();
        kafkaTemplate.send("sync-user", updateEvent);
        log.info("Sent Kafka event: {}", updateEvent);
        log.info("Updated patient records for userId={}", event.getId());
    }

}
