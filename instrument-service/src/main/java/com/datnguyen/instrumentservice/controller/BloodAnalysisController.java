package com.datnguyen.instrumentservice.controller;

import com.datnguyen.instrumentservice.dto.request.BloodAnalysisRequest;
import com.datnguyen.instrumentservice.dto.response.BaseResponse;
import com.datnguyen.instrumentservice.dto.response.BloodAnalysisResponse;
import com.datnguyen.instrumentservice.dto.response.ChangeModeResponse;
import com.datnguyen.instrumentservice.service.BloodAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/blood-analysis")
public class BloodAnalysisController {
    @Autowired
    private BloodAnalysisService bloodAnalysisService;

    @GetMapping("/result/{accessionNumber}")
    public ResponseEntity<?> getBloodAnalysisResult(@PathVariable("accessionNumber") String accessionNumber) {
        BaseResponse baseResponse = new BaseResponse();

        try {
            BloodAnalysisResponse testOrder = bloodAnalysisService.getBloodAnalysisResult(accessionNumber);
            baseResponse.setStatus(200);
            baseResponse.setMessage("Instrument mode changed successfully");
            baseResponse.setData(testOrder);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to change instrument mode: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }
    }
    @PostMapping("hl7")
    public ResponseEntity<?> bloodAnalysisHL7(@RequestBody BloodAnalysisRequest bloodAnalysisRequest) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            BloodAnalysisResponse testOrder = bloodAnalysisService.bloodAnalysisHL7(bloodAnalysisRequest);
            baseResponse.setStatus(200);
            baseResponse.setMessage("Blood sample analyzed successfully.");
            baseResponse.setData(testOrder);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to Blood sample analyzed successfully.: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }
    }
}
