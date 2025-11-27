package ttldd.instrumentservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.instrumentservice.client.WareHouseClient;
import ttldd.instrumentservice.dto.request.ChangeModeRequest;
import ttldd.instrumentservice.dto.response.ChangeModeResponse;
import ttldd.instrumentservice.dto.response.InstrumentResponse;
import ttldd.instrumentservice.dto.response.RestResponse;
import ttldd.instrumentservice.entity.InstrumentModeAudit;
import ttldd.instrumentservice.entity.InstrumentStatus;
import ttldd.instrumentservice.repository.InstrumentModeAuditRepo;
import ttldd.instrumentservice.service.imp.InstrumentServiceImp;
import ttldd.instrumentservice.utils.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceImpTest {

    @Mock private WareHouseClient wareHouseClient;
    @Mock private InstrumentModeAuditRepo instrumentModeAuditRepo;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks private InstrumentServiceImp instrumentService;

    @Test
    void changeInstrumentMode_Success_FromReadyToMaintenance() {
        // Given
        Long instrumentId = 1234567L;
        ChangeModeRequest request = ChangeModeRequest.builder()
                .instrumentId(instrumentId)
                .newMode(InstrumentStatus.MAINTENANCE)
                .reason("Bảo trì định kỳ")
                .build();

        InstrumentResponse current = new InstrumentResponse();
        current.setId(instrumentId);
        current.setStatus(InstrumentStatus.READY);

        InstrumentResponse updated = new InstrumentResponse();
        updated.setId(instrumentId);
        updated.setStatus(InstrumentStatus.MAINTENANCE);

        when(wareHouseClient.getById(instrumentId))
                .thenReturn(RestResponse.<InstrumentResponse>builder().data(current).build());
        when(wareHouseClient.updateStatus(eq(instrumentId), any()))
                .thenReturn(RestResponse.<InstrumentResponse>builder().data(updated).build());
        when(jwtUtils.getFullName()).thenReturn("Nguyen Van A");

        // When
        ChangeModeResponse response = instrumentService.changeInstrumentMode(request);

        // Then
        assertEquals(InstrumentStatus.MAINTENANCE, response.getNewMode());
        assertEquals(InstrumentStatus.READY, response.getPreviousMode());
        assertEquals("Bảo trì định kỳ", response.getReason());
        verify(instrumentModeAuditRepo).save(any(InstrumentModeAudit.class));
    }

    @Test
    void changeInstrumentMode_ToReady_RequireQcConfirmed_ThrowsException() {
        ChangeModeRequest request = ChangeModeRequest.builder()
                .instrumentId(1234567L)
                .newMode(InstrumentStatus.READY)
                .qcConfirmed(false)
                .build();

        InstrumentResponse current = new InstrumentResponse();
        current.setId(1234567L);
        current.setStatus(InstrumentStatus.MAINTENANCE);

        when(wareHouseClient.getById(1234567L))
                .thenReturn(RestResponse.<InstrumentResponse>builder().data(current).build());

        assertThrows(IllegalStateException.class, () ->
                instrumentService.changeInstrumentMode(request));
    }

    @Test
    void changeInstrumentMode_NoReasonForMaintenance_ThrowsException() {
        ChangeModeRequest request = ChangeModeRequest.builder()
                .instrumentId(1234567L)
                .newMode(InstrumentStatus.INACTIVE)
                .reason("")
                .build();

        InstrumentResponse current = new InstrumentResponse();
        current.setId(1234567L);
        current.setStatus(InstrumentStatus.READY);

        when(wareHouseClient.getById(1234567L))
                .thenReturn(RestResponse.<InstrumentResponse>builder().data(current).build());

        assertThrows(RuntimeException.class, () ->
                instrumentService.changeInstrumentMode(request));
    }

    @Test
    void changeInstrumentMode_SameMode_ThrowsException() {
        ChangeModeRequest request = ChangeModeRequest.builder()
                .instrumentId(1234567L)
                .newMode(InstrumentStatus.READY)
                .build();

        InstrumentResponse current = new InstrumentResponse();
        current.setId(1234567L);
        current.setStatus(InstrumentStatus.READY);

        when(wareHouseClient.getById(1234567L))
                .thenReturn(RestResponse.<InstrumentResponse>builder().data(current).build());

        assertThrows(RuntimeException.class, () ->
                instrumentService.changeInstrumentMode(request));
    }
}