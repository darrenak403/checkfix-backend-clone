package com.datnguyen.instrumentservice.controller;

import com.datnguyen.instrumentservice.dto.request.ReagentInstallRequest;
import com.datnguyen.instrumentservice.dto.response.BaseResponse;
import com.datnguyen.instrumentservice.dto.response.ReagentInstallResponse;
import com.datnguyen.instrumentservice.service.imp.ReagentServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//hello
@RestController
@RequestMapping("/reagents")
public class ReagentInstallController {
    @Autowired
    private ReagentServiceImp reagentServiceImp;

    @PostMapping("/install")
    public ResponseEntity<?> installReagent(@Valid @RequestBody ReagentInstallRequest reagentInstallRequest) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            ReagentInstallResponse updateInstrument =  reagentServiceImp.installReagent(reagentInstallRequest);
            baseResponse.setStatus(200);
            baseResponse.setMessage("Reagent installed successfully");
            baseResponse.setData(updateInstrument);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to install reagent: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }

    }
}
