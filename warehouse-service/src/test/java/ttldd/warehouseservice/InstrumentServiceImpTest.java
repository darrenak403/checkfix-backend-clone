package ttldd.warehouseservice;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ttldd.warehouseservice.dto.request.InstrumentRequest;
import ttldd.warehouseservice.dto.request.InstrumentUpdateRequest;
import ttldd.warehouseservice.dto.response.InstrumentResponse;
import ttldd.warehouseservice.dto.response.InstrumentUpdateResponse;
import ttldd.warehouseservice.dto.response.PageResponse;
import ttldd.warehouseservice.entity.Instrument;
import ttldd.warehouseservice.entity.InstrumentStatus;
import ttldd.warehouseservice.mapper.InstrumentMapper;
import ttldd.warehouseservice.repository.InstrumentRepo;
import ttldd.warehouseservice.service.impl.InstrumentServiceImp;
import ttldd.warehouseservice.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceImpTest {

    @Mock private InstrumentRepo instrumentRepo;
    @Mock private InstrumentMapper instrumentMapper;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks private InstrumentServiceImp instrumentService;

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 4, 5, 10, 0);

    @Test
    void createInstrument_Success() {
        // Given
        InstrumentRequest request = InstrumentRequest.builder()
                .name("XN-1000")
                .serialNumber("SN123456")
                .build();

        Instrument entity = Instrument.builder()
                .id(1L)
                .name("XN-1000")
                .serialNumber("SN123456")
                .build();

        Instrument savedEntity = Instrument.builder()
                .id(1L)
                .name("XN-1000")
                .serialNumber("SN123456")
                .status(InstrumentStatus.READY)
                .createdBy("Dr. Nguyễn Văn A")
                .createdAt(NOW)
                .isActive(true)
                .build();

        InstrumentResponse expectedResponse = InstrumentResponse.builder()
                .id(1L)
                .name("XN-1000")
                .status(InstrumentStatus.READY)
                .createdBy("Dr. Nguyễn Văn A")
                .build();

        when(instrumentMapper.toInstrumentEntity(request)).thenReturn(entity);
        when(jwtUtils.getFullName()).thenReturn("Dr. Nguyễn Văn A");
        when(instrumentRepo.save(any(Instrument.class))).thenAnswer(invocation -> {
            Instrument i = invocation.getArgument(0);
            i.setId(1L);
            i.setStatus(InstrumentStatus.READY);
            i.setCreatedBy("Dr. Nguyễn Văn A");
            i.setCreatedAt(NOW);
            i.setActive(true);
            return i;
        });
        when(instrumentMapper.toInstrumentResponse(any(Instrument.class))).thenReturn(expectedResponse);

        // When
        InstrumentResponse response = instrumentService.createInstrument(request);

        // Then
        assertThat(response)
                .isNotNull()
                .extracting(InstrumentResponse::getName, InstrumentResponse::getStatus, InstrumentResponse::getCreatedBy)
                .containsExactly("XN-1000", InstrumentStatus.READY, "Dr. Nguyễn Văn A");

        verify(instrumentRepo).save(any(Instrument.class));
        verify(instrumentMapper).toInstrumentResponse(any(Instrument.class));
    }

    @Test
    void getInstruments_Pagination_Success() {
        // Given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Instrument inst1 = Instrument.builder().id(1L).name("XN-1000").status(InstrumentStatus.READY).build();
        Instrument inst2 = Instrument.builder().id(2L).name("AU5800").status(InstrumentStatus.MAINTENANCE).build();

        Page<Instrument> pageResult = new PageImpl<>(List.of(inst1, inst2), pageable, 25);

        when(instrumentRepo.findAll(pageable)).thenReturn(pageResult);
        when(instrumentMapper.toInstrumentResponse(inst1)).thenReturn(
                InstrumentResponse.builder().id(1L).name("XN-1000").status(InstrumentStatus.READY).build()
        );
        when(instrumentMapper.toInstrumentResponse(inst2)).thenReturn(
                InstrumentResponse.builder().id(2L).name("AU5800").status(InstrumentStatus.MAINTENANCE).build()
        );

        // When
        PageResponse<InstrumentResponse> response = instrumentService.getInstruments(page, size);

        // Then
        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getCurrentPage()).isEqualTo(1);
                    assertThat(r.getPageSize()).isEqualTo(10);
                    assertThat(r.getTotalItems()).isEqualTo(25);
                    assertThat(r.getTotalPages()).isEqualTo(3);
                    assertThat(r.getData()).hasSize(2);
                    assertThat(r.getData().get(0).getName()).isEqualTo("XN-1000");
                });
    }

    @Test
    void getInstrumentById_Success() {
        // Given
        Long id = 1L;
        Instrument entity = Instrument.builder()
                .id(id)
                .name("Cobas 6000")
                .status(InstrumentStatus.READY)
                .createdBy("Admin")
                .build();

        InstrumentResponse expected = InstrumentResponse.builder()
                .id(id)
                .name("Cobas 6000")
                .status(InstrumentStatus.READY)
                .createdBy("Admin")
                .build();

        when(instrumentRepo.findById(id)).thenReturn(Optional.of(entity));
        instrumentMapper.toInstrumentResponse(entity); // stub
        when(instrumentMapper.toInstrumentResponse(entity)).thenReturn(expected);

        // When
        InstrumentResponse response = instrumentService.getInstrumentById(id);

        // Then
        assertThat(response).isEqualTo(expected);
        verify(instrumentRepo).findById(id);
    }

    @Test
    void getInstrumentById_NotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(instrumentRepo.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> instrumentService.getInstrumentById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Instrument not found with id: " + id);
    }

    @Test
    void updateInstrument_Success() {
        // Given
        Long id = 1L;
        Instrument existing = Instrument.builder()
                .id(id)
                .name("Old Name")
                .status(InstrumentStatus.READY)
                .build();

        InstrumentUpdateRequest request = InstrumentUpdateRequest.builder()
                .status(InstrumentStatus.MAINTENANCE)
                .build();

        when(instrumentRepo.findById(id)).thenReturn(Optional.of(existing));
        when(instrumentRepo.save(any(Instrument.class))).thenAnswer(i -> i.getArgument(0));

        // When
        InstrumentUpdateResponse response = instrumentService.updateInstrument(id, request);

        // Then
        assertThat(response)
                .extracting(InstrumentUpdateResponse::getId, InstrumentUpdateResponse::getStatus)
                .containsExactly(id, InstrumentStatus.MAINTENANCE);

        assertThat(existing.getStatus()).isEqualTo(InstrumentStatus.MAINTENANCE);
        assertThat(existing.getUpdatedAt()).isNotNull();
        verify(instrumentRepo).save(existing);
    }

    @Test
    void updateInstrument_NotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(instrumentRepo.findById(id)).thenReturn(Optional.empty());

        InstrumentUpdateRequest request = InstrumentUpdateRequest.builder().build();

        // When & Then
        assertThatThrownBy(() -> instrumentService.updateInstrument(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Instrument not found with id: " + id);
    }

    @Test
    void deleteInstrument_Success_SoftDelete() {
        // Given
        Long id = 1L;
        Instrument instrument = Instrument.builder()
                .id(id)
                .name("To Be Deleted")
                .isActive(true)
                .build();

        when(instrumentRepo.findById(id)).thenReturn(Optional.of(instrument));

        // When
        instrumentService.deleteInstrument(id);

        // Then
        assertThat(instrument.isActive()).isFalse();
        verify(instrumentRepo).save(instrument);
    }

    @Test
    void deleteInstrument_NotFound_ThrowsException() {
        // Given
        Long id = 999L;
        when(instrumentRepo.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> instrumentService.deleteInstrument(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Instrument not found with id: " + id);
    }
}