package jungle.warehouseservice.service.impl;

import jungle.warehouseservice.dto.request.InstrumentRequest;
import jungle.warehouseservice.dto.request.InstrumentUpdateRequest;
import jungle.warehouseservice.dto.response.InstrumentResponse;
import jungle.warehouseservice.dto.response.InstrumentUpdateResponse;
import jungle.warehouseservice.dto.response.PageResponse;
import jungle.warehouseservice.entity.Instrument;
import jungle.warehouseservice.entity.InstrumentStatus;
import jungle.warehouseservice.mapper.InstrumentMapper;
import jungle.warehouseservice.repository.InstrumentRepo;
import jungle.warehouseservice.repository.httpClient.UserClient;
import jungle.warehouseservice.service.InstrumentService;
import jungle.warehouseservice.utils.JwtUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InstrumentServiceImp implements InstrumentService {

    InstrumentMapper instrumentMapper;

    InstrumentRepo instrumentRepo;


    JwtUtils jwtUtils;

    @Override
    public InstrumentResponse createInstrument(InstrumentRequest instrumentRequest) {
        Instrument instrument = instrumentMapper.toInstrumentEntity(instrumentRequest);

        instrument.setCreatedBy(jwtUtils.getFullName());
        instrument.setStatus(InstrumentStatus.READY);
        instrument.setCreatedAt(LocalDateTime.now());
        instrumentRepo.save(instrument);
        return instrumentMapper.toInstrumentResponse(instrument);
    }

    @Override
    public PageResponse<InstrumentResponse> getInstruments(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var instrumentPage = instrumentRepo.findAll(pageable);
        return PageResponse.<InstrumentResponse>builder()
                .totalPages(instrumentPage.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .totalItems(instrumentPage.getTotalElements())
                .data(instrumentPage.getContent().stream().map(instrumentMapper::toInstrumentResponse).toList())
                .build();
    }

    @Override
    public InstrumentResponse getInstrumentById(Long id) {
        Instrument instrument = instrumentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instrument not found with id: " + id));
        return instrumentMapper.toInstrumentResponse(instrument);
    }

    @Override
    public InstrumentUpdateResponse updateInstrument(Long id, InstrumentUpdateRequest instrumentUpdateRequest) {
        Instrument instrument = instrumentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instrument not found with id: " + id));

        instrument.setStatus(instrumentUpdateRequest.getStatus());
        instrument.setUpdatedAt(LocalDateTime.now());

        instrumentRepo.save(instrument);

        return InstrumentUpdateResponse.builder()
                .id(instrument.getId())
                .name(instrument.getName())
                .serialNumber(instrument.getSerialNumber())
                .status(instrument.getStatus())
                .build();
    }
}
