package ttldd.instrumentservice.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ttldd.event.dto.RawHL7MessageDTO;
import ttldd.instrumentservice.client.TestOrderClient;
import ttldd.instrumentservice.client.TestOrderDTO;
import ttldd.instrumentservice.client.WareHouseClient;
import ttldd.instrumentservice.dto.request.BloodAnalysisRequest;
import ttldd.instrumentservice.dto.response.BloodAnalysisResponse;
import ttldd.instrumentservice.dto.response.InstrumentResponse;
import ttldd.instrumentservice.dto.response.RestResponse;
import ttldd.instrumentservice.entity.ReagentEntity;
import ttldd.instrumentservice.entity.ReagentStatus;
import ttldd.instrumentservice.producer.RawHL7Producer;
import ttldd.instrumentservice.repository.ReagentRepo;
import ttldd.instrumentservice.service.BloodAnalysisService;
import ttldd.instrumentservice.utils.HL7Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttldd.instrumentservice.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class BloodAnalysisServiceImp implements BloodAnalysisService {

    private final TestOrderClient testOrderClient;


    private final ReagentRepo reagentRepo;


    private final HL7Utils hl7Util;


    private final RawHL7Producer rawHL7Producer;


    private final JwtUtils jwtUtils;

    private final WareHouseClient wareHouseClient;



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


        RawHL7MessageDTO hl7DTO = RawHL7MessageDTO.builder()
                .service("instrument-service")
                .accessionNumber(bloodAnalysisRequest.getAccessionNumber())
                .testOrderId(String.valueOf(testOrder.getData().getId()))
                .instrumentName(getInstrumentName(bloodAnalysisRequest.getAccessionNumber()))
                .hl7Message(hl7Message)
                .generatedBy(jwtUtils.getFullName() != null ? jwtUtils.getFullName() : "SYSTEM")
                .timestamp(LocalDateTime.now())
                .traceId(UUID.randomUUID().toString())
                .build();
        rawHL7Producer.sendRawHL7(hl7DTO);
        log.info("📤 Sent HL7 message to Monitoring for accession {}", bloodAnalysisRequest.getAccessionNumber());

        return BloodAnalysisResponse.builder()
                .status("SUCCESS")
                .hl7Message(hl7Message)
                .testOrderId(testOrder.getData().getId())
                .instrumentStatus("Available")
                .build();
    }

    private String getInstrumentName(String accessionNumber) {
        RestResponse<TestOrderDTO> testOrderResponse = testOrderClient.getTestOrdersByAccessionNumber(accessionNumber);
        if (testOrderResponse == null || testOrderResponse.getData() == null) {
            throw new RuntimeException("Test order not found for accession number: " + accessionNumber);
        }
        RestResponse<InstrumentResponse> instrument = wareHouseClient.getById(testOrderResponse.getData().getInstrumentId());
        return instrument.getData().getName();
    }

}
