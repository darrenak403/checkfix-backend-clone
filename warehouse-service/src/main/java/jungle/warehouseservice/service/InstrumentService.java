package jungle.warehouseservice.service;

import jungle.warehouseservice.dto.request.InstrumentRequest;
import jungle.warehouseservice.dto.response.InstrumentResponse;
import org.springframework.stereotype.Service;

@Service
public interface InstrumentService {
    InstrumentResponse createInstrument(InstrumentRequest instrumentRequest);
}
