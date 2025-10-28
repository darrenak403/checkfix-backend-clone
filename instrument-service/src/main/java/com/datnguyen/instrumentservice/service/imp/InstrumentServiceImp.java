package com.datnguyen.instrumentservice.service.imp;

import com.datnguyen.instrumentservice.client.WareHouseClient;
import com.datnguyen.instrumentservice.dto.request.ChangeModeRequest;
import com.datnguyen.instrumentservice.dto.request.InstrumentUpdateRequest;
import com.datnguyen.instrumentservice.dto.response.ChangeModeResponse;
import com.datnguyen.instrumentservice.dto.response.InstrumentResponse;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import com.datnguyen.instrumentservice.entity.InstrumentModeAudit;
import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import com.datnguyen.instrumentservice.repository.InstrumentModeAuditRepo;
import com.datnguyen.instrumentservice.service.InstrumentService;

import com.datnguyen.instrumentservice.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

//hello
@Service
public class InstrumentServiceImp implements InstrumentService {
    @Autowired
    private WareHouseClient wareHouseClient;

    @Autowired
    private InstrumentModeAuditRepo instrumentModeAuditRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    @Override
    public ChangeModeResponse changeInstrumentMode(ChangeModeRequest changeModeRequest) {
        RestResponse<InstrumentResponse> instrument = wareHouseClient.getById(changeModeRequest.getInstrumentId());
        if(instrument == null || instrument.getData() == null) {
            throw new RuntimeException("Instrument must exist in the system");
        }
        InstrumentStatus currentMode = instrument.getData().getStatus();

        if (currentMode.equals(changeModeRequest.getNewMode())) {
            throw new RuntimeException("New mode must be different from current mode");
        }

        if ((changeModeRequest.getNewMode().equals(InstrumentStatus.MAINTENANCE)
                || changeModeRequest.getNewMode().equals(InstrumentStatus.INACTIVE))
                && (changeModeRequest.getReason() == null || changeModeRequest.getReason().isEmpty())) {
            throw new RuntimeException("Reason must be provided when changing mode to INACTIVE or MAINTENANCE");
        }

        if (changeModeRequest.getNewMode().equals(InstrumentStatus.READY)) {
            if (changeModeRequest.getQcConfirmed() == null || Boolean.FALSE.equals(changeModeRequest.getQcConfirmed())) {
                throw new IllegalStateException("QC confirmation required before switching to READY mode");
            }
            System.out.println(" QC check confirmed before switching to READY mode");
        }

        //Update instrument mode nè -_-
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        instrumentUpdateRequest.setStatus(changeModeRequest.getNewMode());

        RestResponse<InstrumentResponse> instrumentUpdate = wareHouseClient.updateStatus(changeModeRequest.getInstrumentId(), instrumentUpdateRequest);

        //Create audit log
        InstrumentModeAudit instrumentModeAudit = new InstrumentModeAudit();

        instrumentModeAudit.setId(UUID.randomUUID().toString());
        instrumentModeAudit.setInstrumentId(instrumentUpdate.getData().getId());
        instrumentModeAudit.setPreviousMode(currentMode);
        instrumentModeAudit.setNewMode(instrumentUpdate.getData().getStatus());
        instrumentModeAudit.setChangedBy(jwtUtils.getFullName());
        instrumentModeAudit.setReason(changeModeRequest.getReason());

        instrumentModeAuditRepo.save(instrumentModeAudit);


        return ChangeModeResponse.builder()
                .instrumentId(instrumentUpdate.getData().getId())
                .previousMode(currentMode)
                .newMode(instrumentUpdate.getData().getStatus())
                .changedBy(jwtUtils.getFullName())
                .reason(changeModeRequest.getReason())
                .timestamp(LocalDateTime.now())
                .message("Instrument mode changed successfully")
                .build();
    }

}

