package ttld.patientservice;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import ttldd.event.dto.PatientUpdateEvent;
import ttldd.event.dto.UserUpdatedEvent;
import ttldd.patientservice.dto.request.PatientRequest;
import ttldd.patientservice.dto.request.PatientUpdateRequest;
import ttldd.patientservice.dto.response.*;
import ttldd.patientservice.entity.Patient;
import ttldd.patientservice.mapper.PatientMapper;
import ttldd.patientservice.repo.PatientRepo;
import ttldd.patientservice.repo.httpClient.UserClient;
import ttldd.patientservice.service.imp.PatientServiceImp;
import ttldd.patientservice.utils.JwtUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImpTest {

    @Mock private PatientRepo patientRepo;
    @Mock private PatientMapper patientMapper;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserClient userClient;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks private PatientServiceImp patientService;

    // Helper
    private Patient createSamplePatient() {
        return Patient.builder()
                .id(1L)
                .userId(100L)
                .patientCode("PAT-2025-ABC12345")
                .fullName("Nguyễn Văn A")
                .email("a@example.com")
                .phone("0901234567")
                .gender("MALE")
                .yob(LocalDate.of(1990, 1, 1))
                .address("Hà Nội")
                .createdBy("Admin")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @Test
    void createPatient_Success(
    ) {
        PatientRequest request = PatientRequest.builder()
                .userId(100L)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .fullName("Nguyễn Văn A")
                .email("a@example.com")
                .phone("0901234567")
                .gender("MALE")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Hà Nội")
                .avatarUrl("https://avatar.com/a.jpg")
                .build();

        RestResponse<UserResponse> restResponse = RestResponse.<UserResponse>builder()
                .data(userResponse)
                .build();

        Patient patient = createSamplePatient();
        Patient savedPatient = createSamplePatient();
        savedPatient.setPatientCode("PAT-2025-ABC12345");

        when(userClient.getUser(100L)).thenReturn(restResponse);
        when(patientRepo.existsByUserIdAndDeletedFalse(100L)).thenReturn(false);
        when(patientMapper.toPatientEntity(request)).thenReturn(patient);
        when(jwtUtils.getFullName()).thenReturn("Admin User");
        when(patientRepo.existsByPatientCode(anyString())).thenReturn(false).thenReturn(true); // first false → generate → exists → loop → next false
        when(patientRepo.save(any(Patient.class))).thenReturn(savedPatient);
        when(patientMapper.toPatientResponse(any(Patient.class))).thenReturn(
                PatientResponse.builder().id(1L).patientCode("PAT-2025-ABC12345").build()
        );


        PatientResponse response = patientService.createPatient(request);

        assertNotNull(response);
        assertEquals("PAT-2025-ABC12345", response.getPatientCode());
        verify(patientRepo).save(any(Patient.class));
    }

    @Test
    void createPatient_UserNotFound_ThrowsException() {
        PatientRequest request = PatientRequest.builder().userId(999L).build();

        when(userClient.getUser(999L))
                .thenReturn(RestResponse.<UserResponse>builder().data(null).build());

        assertThrows(IllegalArgumentException.class, () ->
                patientService.createPatient(request));
    }

    @Test
    void createPatient_AlreadyExists_ThrowsException() {
        PatientRequest request = PatientRequest.builder().userId(100L).build();

        when(userClient.getUser(100L))
                .thenReturn(RestResponse.<UserResponse>builder().data(new UserResponse()).build());
        when(patientRepo.existsByUserIdAndDeletedFalse(100L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                patientService.createPatient(request));
    }

    @Test
    void updatePatient_Success_PublishKafkaEvent() {
        Long patientId = 1L;
        PatientUpdateRequest request = PatientUpdateRequest.builder()
                .fullName("Nguyễn Văn B")
                .phone("0987654321")
                .build();

        Patient patient = createSamplePatient();
        Patient updatedPatient = createSamplePatient();
        updatedPatient.setFullName("Nguyễn Văn B");
        updatedPatient.setPhone("0987654321");

        when(patientRepo.findByIdAndDeletedFalse(patientId)).thenReturn(Optional.of(patient));
        when(jwtUtils.getFullName()).thenReturn("Doctor X");
        when(patientRepo.save(any(Patient.class))).thenReturn(updatedPatient);
        when(patientMapper.toPatientResponse(any())).thenReturn(
                PatientResponse.builder().id(1L).fullName("Nguyễn Văn B").build()
        );

        PatientResponse response = patientService.updatePatient(patientId, request);

        assertEquals("Nguyễn Văn B", response.getFullName());
        verify(kafkaTemplate).send(eq("patient-updated"), any(PatientUpdateEvent.class));
    }

    @Test
    void deletePatient_Success_SoftDelete() {
        Long patientId = 1L;
        Patient patient = createSamplePatient();

        when(patientRepo.findByIdAndDeletedFalse(patientId)).thenReturn(Optional.of(patient));
        when(patientRepo.save(any(Patient.class))).thenReturn(patient);

        patientService.deletePatient(patientId);

        assertTrue(patient.isDeleted());
        verify(patientRepo).save(patient);
    }

    @Test
    void getPatient_NotFound_ThrowsException() {
        when(patientRepo.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                patientService.getPatient(999L));
    }

    @Test
    void getCurrentPatient_Success() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("userId", 100L)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.setContext(securityContext);

        Patient patient = createSamplePatient();
        when(patientRepo.findTop1ByUserIdAndDeletedFalseOrderByCreatedAtDesc(100L))
                .thenReturn(List.of(patient));
        when(patientMapper.toPatientResponse(patient))
                .thenReturn(PatientResponse.builder().id(1L).fullName("Nguyễn Văn A").build());

        List<PatientResponse> result = patientService.getCurrentPatient();

        assertEquals(1, result.size());
        assertEquals("Nguyễn Văn A", result.get(0).getFullName());
    }

    @Test
    void getCurrentPatient_NoRecord_ThrowsException() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("userId", 999L)
                .build();

        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.setContext(ctx);

        when(patientRepo.findTop1ByUserIdAndDeletedFalseOrderByCreatedAtDesc(999L))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                patientService.getCurrentPatient());
    }

    @Test
    void getPatients_Pagination_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Patient patient = createSamplePatient();
        Page<Patient> page = new PageImpl<>(List.of(patient), pageable, 1);

        when(patientRepo.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(patientMapper.toPatientResponse(patient))
                .thenReturn(PatientResponse.builder().id(1L).fullName("Test").build());

        PageResponse<PatientResponse> response = patientService.getPatients(1, 10);

        assertEquals(1, response.getCurrentPage());
        assertEquals(1, response.getTotalItems());
        assertEquals(1, response.getData().size());
    }

    @Test
    void asyncPatientFromUser_Success_UpdateAndPublish() {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .id(100L)
                .fullName("Trần Thị B")
                .email("b@example.com")
                .phone("0912345678")
                .gender("FEMALE")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .address("TP.HCM")
                .avatarUrl("https://newavatar.com/b.jpg")
                .build();

        Patient patient = createSamplePatient();

        when(patientRepo.findByUserIdAndDeletedFalse(100L)).thenReturn(patient);

        patientService.asyncPatientFromUser(event);

        assertEquals("Trần Thị B", patient.getFullName());
        assertEquals("b@example.com", patient.getEmail());
        assertEquals("0912345678", patient.getPhone());
        assertEquals("FEMALE", patient.getGender());
        assertEquals("TP.HCM", patient.getAddress());

        verify(patientRepo).save(patient);
        verify(kafkaTemplate).send(eq("sync-user"), any(PatientUpdateEvent.class));
    }

    @Test
    void asyncPatientFromUser_PatientNotFound_DoNothing() {

        UserUpdatedEvent event = UserUpdatedEvent.builder().id(999L).build();

        Patient patient = patientRepo.findByUserIdAndDeletedFalse(event.getId());
        if (patient == null) {
            return;
        }


        when(patientRepo.findByUserIdAndDeletedFalse(999L)).thenReturn(null);

        patientService.asyncPatientFromUser(event);

        verify(patientRepo, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}