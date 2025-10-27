package com.datnguyen.instrumentservice.service;

import com.datnguyen.instrumentservice.dto.request.ChangeModeRequest;
import com.datnguyen.instrumentservice.dto.response.ChangeModeResponse;
import org.springframework.stereotype.Service;

@Service
public interface InstrumentService {
    ChangeModeResponse changeInstrumentMode(ChangeModeRequest changeModeRequest);

}
