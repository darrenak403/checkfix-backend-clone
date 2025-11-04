package ttldd.warehouseservice.service.impl;

import ttldd.warehouseservice.dto.request.InstrumentRequest;
import ttldd.warehouseservice.dto.request.InstrumentUpdateRequest;
import ttldd.warehouseservice.dto.response.InstrumentResponse;
import ttldd.warehouseservice.dto.response.InstrumentUpdateResponse;
import ttldd.warehouseservice.dto.response.PageResponse;
import ttldd.warehouseservice.entity.Instrument;
import ttldd.warehouseservice.entity.InstrumentStatus;
import ttldd.warehouseservice.mapper.InstrumentMapper;
import ttldd.warehouseservice.repository.InstrumentRepo;
import ttldd.warehouseservice.service.InstrumentService;
import ttldd.warehouseservice.utils.JwtUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
