package jungle.warehouseservice.service.impl;

import jungle.warehouseservice.dto.request.InstrumentRequest;
import jungle.warehouseservice.dto.response.InstrumentResponse;
import jungle.warehouseservice.entity.Instrument;
import jungle.warehouseservice.entity.InstrumentStatus;
import jungle.warehouseservice.mapper.InstrumentMapper;
import jungle.warehouseservice.repository.InstrumentRepo;
import jungle.warehouseservice.service.InstrumentService;
import jungle.warehouseservice.utils.JwtUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
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

        instrument.setCreatedBy(jwtUtils.getCurrentUserId());
        instrument.setStatus(InstrumentStatus.READY);
        instrument.setCreatedAt(LocalDateTime.now());
        log.info("User create: {}", jwtUtils.getCurrentUserId());
        instrumentRepo.save(instrument);
        return instrumentMapper.toInstrumentResponse(instrument);
    }
}
