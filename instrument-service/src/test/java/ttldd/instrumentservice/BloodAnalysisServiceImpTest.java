package ttldd.instrumentservice;

import org.mockito.quality.Strictness;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.transaction.Transactional;
import org.mockito.junit.jupiter.MockitoSettings;
import ttldd.instrumentservice.client.TestOrderDTO;
import ttldd.instrumentservice.dto.request.BloodAnalysisRequest;
import ttldd.instrumentservice.dto.response.BloodAnalysisResponse;
import ttldd.instrumentservice.dto.response.RestResponse;
import ttldd.instrumentservice.entity.ReagentEntity;
import ttldd.instrumentservice.entity.ReagentStatus;
import ttldd.instrumentservice.repository.ReagentRepo;
import ttldd.instrumentservice.repository.RawHL7TestResultRepo;
import ttldd.instrumentservice.service.imp.BloodAnalysisServiceImp;
import ttldd.instrumentservice.utils.HL7Utils;
import ttldd.instrumentservice.utils.JwtUtils;
import ttldd.instrumentservice.client.TestOrderClient;
import ttldd.instrumentservice.client.WareHouseClient;
import ttldd.instrumentservice.producer.RawHL7Producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BloodAnalysisServiceImpTest {

    @Mock private TestOrderClient testOrderClient;
    @Mock private ReagentRepo reagentRepo;
    @Mock private HL7Utils hl7Util;
    @Mock private RawHL7Producer rawHL7Producer;
    @Mock private RawHL7TestResultRepo rawHL7TestResultRepo;
    @Mock private JwtUtils jwtUtils;
    @Mock private WareHouseClient wareHouseClient;

    @InjectMocks private BloodAnalysisServiceImp bloodAnalysisService;

    @Test
    @Transactional
    void bloodAnalysisHL7_Success_ConsumesReagent_SendsHL7() {
        BloodAnalysisRequest request = BloodAnalysisRequest.builder()
                .accessionNumber("ACC2025001")
                .reagentId("reag-001")
                .build();

        ReagentEntity reagent = ReagentEntity.builder()
                .id("reag-001")
                .quantity(20)
                .status(ReagentStatus.AVAILABLE)
                .build();

        TestOrderDTO testOrderDTO = TestOrderDTO.builder()
                .id(100L)
                .accessionNumber("ACC2025001")
                .instrumentName("Hematology Analyzer")
                .build();

        when(reagentRepo.findByIdAndStatus("reag-001", ReagentStatus.AVAILABLE))
                .thenReturn(java.util.Optional.of(reagent));
        when(rawHL7TestResultRepo.existsByAccessionNumber("ACC2025001")).thenReturn(false);
        when(testOrderClient.getTestOrdersByAccessionNumber("ACC2025001"))
                .thenReturn(RestResponse.<TestOrderDTO>builder().data(testOrderDTO).build());
        when(hl7Util.generateBloodIndicators()).thenReturn("WBC|10.5");
        when(hl7Util.generateHL7(any(), any())).thenReturn("MSH|^~\\&|...");

        when(jwtUtils.getFullName()).thenReturn("Lab Tech");

        BloodAnalysisResponse response = bloodAnalysisService.bloodAnalysisHL7(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(15.0, reagent.getQuantity()); // giảm 5
        verify(reagentRepo).save(reagent);
        verify(rawHL7Producer).sendRawHL7(any());
        verify(rawHL7TestResultRepo).save(any());
    }

    @Test
    void bloodAnalysisHL7_ReagentNotEnough_ThrowsException() {
        BloodAnalysisRequest request = BloodAnalysisRequest.builder()
                .reagentId("reag-low")
                .accessionNumber("ACC001")
                .build();

        ReagentEntity reagent = ReagentEntity.builder()
                .id("reag-low")
                .quantity(3)
                .status(ReagentStatus.AVAILABLE)
                .build();

        when(reagentRepo.findByIdAndStatus("reag-low", ReagentStatus.AVAILABLE))
                .thenReturn(java.util.Optional.of(reagent));

        assertThrows(RuntimeException.class, () ->
                bloodAnalysisService.bloodAnalysisHL7(request));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void bloodAnalysisHL7_DuplicateAccessionNumber_ThrowsException() {
        BloodAnalysisRequest request = BloodAnalysisRequest.builder()
                .accessionNumber("ACC_DUPLICATE")
                .reagentId("reag-001")
                .build();

        when(rawHL7TestResultRepo.existsByAccessionNumber("ACC_DUPLICATE")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                bloodAnalysisService.bloodAnalysisHL7(request));
    }
}