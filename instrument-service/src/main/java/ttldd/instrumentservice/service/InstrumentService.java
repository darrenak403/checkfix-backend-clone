package ttldd.instrumentservice.service;

import ttldd.instrumentservice.dto.request.ChangeModeRequest;
import ttldd.instrumentservice.dto.response.ChangeModeResponse;
import org.springframework.stereotype.Service;
//hello
@Service
public interface InstrumentService {
    ChangeModeResponse changeInstrumentMode(ChangeModeRequest changeModeRequest);

}
