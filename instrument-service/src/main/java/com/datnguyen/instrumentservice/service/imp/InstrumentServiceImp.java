package com.datnguyen.instrumentservice.service.imp;

import com.datnguyen.instrumentservice.client.WareHouseClient;
import com.datnguyen.instrumentservice.client.WareHouseDTO;
import com.datnguyen.instrumentservice.dto.request.ChangeModeRequest;
import com.datnguyen.instrumentservice.dto.response.ChangeModeResponse;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import com.datnguyen.instrumentservice.service.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstrumentServiceImp implements InstrumentService {
    @Autowired
    private WareHouseClient wareHouseClient;

    @Override
    public ChangeModeResponse changeInstrumentMode(ChangeModeRequest changeModeRequest) {
        RestResponse<WareHouseDTO> instrument = wareHouseClient.getById(changeModeRequest.getInstrumentId());
        InstrumentStatus currentMode = instrument.getData().getStatus();
        if (currentMode.equals(changeModeRequest.getNewMode())) {
            throw new RuntimeException("New mode must be different from current mode");
        }
        return null;
    }
}

