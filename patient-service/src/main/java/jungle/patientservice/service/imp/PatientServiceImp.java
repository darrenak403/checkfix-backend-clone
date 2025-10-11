package jungle.patientservice.service.imp;

import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.request.PatientUpdateRequest;
import jungle.patientservice.dto.response.*;
import jungle.patientservice.entity.Patient;
import jungle.patientservice.mapper.PatientMapper;
import jungle.patientservice.repo.PatientRepo;
import jungle.patientservice.repo.httpClient.TestOrderClient;
import jungle.patientservice.repo.httpClient.UserClient;
import jungle.patientservice.service.PatientService;
import jungle.patientservice.utils.JwtUtils;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImp implements PatientService {

    private final PatientRepo patientRepo;

    private final PatientMapper patientMapper;

    private final JwtUtils jwtUtils;

    private final UserClient userClient;

    private final TestOrderClient testOrderClient;

    @Override
    public PatientResponse createPatient(PatientRequest patientDTO) {
        RestResponse<UserResponse> user = userClient.getUser(patientDTO.getUserId());
        if (user.getData() == null) {
            throw new IllegalArgumentException("UserId not found: " + patientDTO.getUserId());
        }
        Patient patient = patientMapper.toPatientEntity(patientDTO);
        String patientCode = patientRepo.findFirstByUserIdAndDeletedFalse(patient.getUserId())
                .map(Patient::getPatientCode)
                .filter(StringUtils::hasText)
                .orElseGet(this::generatePatientCode);

        patient.setPatientCode(patientCode);
        patient.setFullName(user.getData().getFullName());
        patient.setEmail(user.getData().getEmail());
        patient.setPhone(user.getData().getPhone());
        patient.setGender(user.getData().getGender());
        patient.setAddress(user.getData().getAddress());
        patient.setYob(user.getData().getDateOfBirth());
        patient.setCreatedBy(jwtUtils.getFullName());
        patient.setCreatedAt(LocalDateTime.now());

        patientRepo.save(patient);
        return patientMapper.toPatientResponse(patient);
    }


    private String generatePatientCode(){
        String shortUUID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "PAT-2025-" + shortUUID;
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
        if (patientDTO.getLastTestDate() != null) {
            patient.setLastTestDate(patientDTO.getLastTestDate());
        }
        if (StringUtils.hasText(patientDTO.getLastTestType())) {
            patient.setLastTestType(patientDTO.getLastTestType());
        }
        if (StringUtils.hasText(patientDTO.getInstrumentUsed())) {
            patient.setInstrumentUsed(patientDTO.getInstrumentUsed());
        }
        patient.setModifiedBy(jwtUtils.getFullName());
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
    public PageResponse<TestOrderResponse> getTestOrdersByPatientId(int page, int size) {
        RestResponse<List<TestOrderResponse>> ordersResponse = testOrderClient.getOrdersByPatientId(jwtUtils.getCurrentUserId());
        List<TestOrderResponse> allOrders = ordersResponse.getData();
        if (allOrders.isEmpty()) {
            return PageResponse.<TestOrderResponse>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalPages(0)
                    .totalItems(0)
                    .data(Collections.emptyList())
                    .build();
        }
        allOrders.sort(Comparator.comparing(TestOrderResponse::getCreatedAt).reversed());
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TestOrderResponse> orderPage = getTestOrdersPage(allOrders, pageable);
        return PageResponse.<TestOrderResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(orderPage.getTotalPages())
                .totalItems(orderPage.getTotalElements())
                .data(orderPage.getContent())
                .build();
    }

    private Page<TestOrderResponse> getTestOrdersPage(List<TestOrderResponse> allOrders, Pageable pageable) {
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), allOrders.size());

        List<TestOrderResponse> pageContent = (startIndex > allOrders.size())
                ? Collections.emptyList()
                : allOrders.subList(startIndex, endIndex);

        return new PageImpl<>(pageContent, pageable, allOrders.size());
    }

}
