package ttldd.warehouseservice.service;

import ttldd.warehouseservice.dto.request.InstrumentRequest;
import ttldd.warehouseservice.dto.request.InstrumentUpdateRequest;
import ttldd.warehouseservice.dto.response.InstrumentResponse;
import ttldd.warehouseservice.dto.response.InstrumentUpdateResponse;
import ttldd.warehouseservice.dto.response.PageResponse;
import org.springframework.stereotype.Service;

@Service
public interface InstrumentService {
    InstrumentResponse createInstrument(InstrumentRequest instrumentRequest);
    PageResponse<InstrumentResponse> getInstruments(int page, int size);
    InstrumentResponse getInstrumentById(Long id);
    InstrumentUpdateResponse updateInstrument(Long id, InstrumentUpdateRequest instrumentUpdateRequest);
    void deleteInstrument(Long id);
}
