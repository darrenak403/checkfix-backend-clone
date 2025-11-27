package ttldd.instrumentservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.request.UpdateReagentStatusRequest;
import ttldd.instrumentservice.dto.response.ReagentInstallResponse;
import ttldd.instrumentservice.entity.ReagentEntity;
import ttldd.instrumentservice.entity.ReagentStatus;
import ttldd.instrumentservice.entity.ReagentType;
import ttldd.instrumentservice.repository.ReagentAuditLogRepo;
import ttldd.instrumentservice.repository.ReagentHistoryRepo;
import ttldd.instrumentservice.repository.ReagentRepo;
import ttldd.instrumentservice.repository.ReagentUpdateAuditLogRepo;
import ttldd.instrumentservice.service.imp.ReagentServiceImp;
import ttldd.instrumentservice.utils.JwtUtils;

import java.time.LocalDate;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReagentServiceImpTest {

    @Mock private ReagentRepo reagentRepo;
    @Mock
    private JwtUtils jwtUtils;
    @Mock private ReagentHistoryRepo reagentHistoryRepo;
    @Mock private ReagentAuditLogRepo reagentAuditLogRepo;
    @Mock private ReagentUpdateAuditLogRepo reagentUpdateAuditLogRepo;

    @InjectMocks
    private ReagentServiceImp reagentService;

    @Test
    void installReagent_Success() {
        ReagentInstallRequest request = ReagentInstallRequest.builder()
                .reagentType(ReagentType.CLEANER)
                .reagentName("Glucose")
                .lotNumber("LOT2025")
                .quantity(100)
                .unit("ml")
                .expiryDate(LocalDate.now().plusYears(1))
                .vendorId("VEN001")
                .vendorName("ABC Corp")
                .build();

        when(jwtUtils.getFullName()).thenReturn("Admin User");
        when(reagentRepo.findByLotNumberAndDeletedFalse("LOT2025")).thenReturn(java.util.Optional.empty());
        when(reagentRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ReagentInstallResponse response = reagentService.installReagent(request);

        assertNotNull(response.getReagentId());
        assertEquals("LOT2025", response.getLotNumber());
        verify(reagentHistoryRepo).save(any());
        verify(reagentAuditLogRepo).save(any());
    }

    @Test
    void installReagent_DuplicateLotNumber_ThrowsException() {
        ReagentInstallRequest request = ReagentInstallRequest.builder()
                .lotNumber("EXISTING_LOT")
                .build();

        when(reagentRepo.findByLotNumberAndDeletedFalse("EXISTING_LOT"))
                .thenReturn(java.util.Optional.of(new ReagentEntity()));

        assertThrows(RuntimeException.class, () ->
                reagentService.installReagent(request));
    }

    @Test
    void updateReagentStatus_QuantityBelow5_CannotSetAvailable() {
        UpdateReagentStatusRequest request = UpdateReagentStatusRequest.builder()
                .reagentStatus(ReagentStatus.AVAILABLE)
                .quantity(3)
                .build();

        ReagentEntity entity = ReagentEntity.builder()
                .id("reg-123")
                .status(ReagentStatus.AVAILABLE)
                .quantity(10)
                .reagentName("Test Reagent")
                .build();

        when(reagentRepo.findByIdAndDeletedFalse("reg-123")).thenReturn(java.util.Optional.of(entity));

        assertThrows(RuntimeException.class, () ->
                reagentService.updateReagentStatus(request, "reg-123"));
    }
}