package jungle.warehouseservice.service;

import jungle.warehouseservice.dto.request.InstrumentRequest;
import jungle.warehouseservice.dto.request.InstrumentUpdateRequest;
import jungle.warehouseservice.dto.response.InstrumentResponse;
import jungle.warehouseservice.dto.response.InstrumentUpdateResponse;
import jungle.warehouseservice.dto.response.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public interface InstrumentService {
    InstrumentResponse createInstrument(InstrumentRequest instrumentRequest);
    PageResponse<InstrumentResponse> getInstruments(int page, int size);
    InstrumentResponse getInstrumentById(Long id);
    InstrumentUpdateResponse updateInstrument(Long id, InstrumentUpdateRequest instrumentUpdateRequest);
}
