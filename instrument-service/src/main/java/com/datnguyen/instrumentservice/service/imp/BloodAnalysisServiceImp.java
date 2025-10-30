package com.datnguyen.instrumentservice.service.imp;

import com.datnguyen.instrumentservice.client.TestOrderClient;
import com.datnguyen.instrumentservice.client.TestOrderDTO;
import com.datnguyen.instrumentservice.dto.request.BloodAnalysisRequest;
import com.datnguyen.instrumentservice.dto.response.BloodAnalysisResponse;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import com.datnguyen.instrumentservice.entity.ReagentEntity;
import com.datnguyen.instrumentservice.entity.ReagentStatus;
import com.datnguyen.instrumentservice.repository.ReagentRepo;
import com.datnguyen.instrumentservice.service.BloodAnalysisService;
import com.datnguyen.instrumentservice.utils.HL7Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BloodAnalysisServiceImp implements BloodAnalysisService {
    @Autowired
    private TestOrderClient testOrderClient;

    @Autowired
    private ReagentRepo reagentRepo;

    @Autowired
    private HL7Utils hl7Util;

    @Override
    public BloodAnalysisResponse getBloodAnalysisResult(String accessionNumber) {
        RestResponse<TestOrderDTO> testOrder = testOrderClient.getTestOrdersByAccessionNumber(accessionNumber);
        return BloodAnalysisResponse.builder()
                .data(testOrder.getData().getAccessionNumber())
                .build();
    }

    @Override
    @Transactional
    public BloodAnalysisResponse bloodAnalysisHL7(BloodAnalysisRequest bloodAnalysisRequest) {
        ReagentEntity reagentEntity = reagentRepo
                .findByIdAndStatus(bloodAnalysisRequest.getReagentId(), ReagentStatus.AVAILABLE)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Reagent not found with ID: " + bloodAnalysisRequest.getReagentId()
                ));
        if (reagentEntity == null || reagentEntity.getQuantity() < 5.0) {
            throw new RuntimeException("Not enough Quantity or Reagent USED for blood analysis.");
        }
        reagentEntity.setQuantity(reagentEntity.getQuantity() - (int) 5.0);
        if (reagentEntity.getQuantity() <= 0) {
            reagentEntity.setStatus(ReagentStatus.OUT_OF_STOCK);
        }
        reagentRepo.save(reagentEntity);

        RestResponse<TestOrderDTO> testOrder = testOrderClient.getTestOrdersByAccessionNumber(bloodAnalysisRequest.getAccessionNumber());
        if (testOrder == null || testOrder.getData() == null) {
            throw new RuntimeException("Test order not found for accession number: " + bloodAnalysisRequest.getAccessionNumber());
        }
        String sampleData = hl7Util.generateBloodIndicators();
        String hl7Message = hl7Util.generateHL7(testOrder, sampleData);

        return BloodAnalysisResponse.builder()
                .status("SUCCESS")
                .hl7Message(hl7Message)
                .testOrderId(testOrder.getData().getId())
                .instrumentStatus("Available")
                .build();
    }




}
