package ttldd.testorderservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.testorderservices.dto.response.RestResponse;
import ttldd.testorderservices.entity.OrderStatus;
import ttldd.testorderservices.entity.TestOrder;
import ttldd.testorderservices.entity.TestResult;
import ttldd.testorderservices.mapper.TestResultMapper;
import ttldd.testorderservices.repository.TestOrderRepository;
import ttldd.testorderservices.repository.TestResultRepository;
import ttldd.testorderservices.service.imp.TestResultService;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestResultServiceTest {

    @Mock
    private TestOrderRepository orderRepo;
    @Mock 
    private TestResultRepository resultRepo;
    @Mock 
    private TestResultMapper mapper;

    @InjectMocks
    private TestResultService testResultService;

    private static final String SAMPLE_HL7 = """
            MSH|^~\\&|XN1000|LAB|||202504011200||ORU^R01|123|P|2.5
            PID|1||12345||Nguyen^Van^A
            OBR|1||ACC001|HEMATOLOGY
            OBX|1|NM|WBC^White Blood Cell||10.5|10^3/uL|4.0-11.0||N
            OBX|2|NM|RBC^Red Blood Cell||5.2|10^6/uL|4.5-5.9||N
            """;

    @Test
    void receiveHl7_Success_ParseAndSaveResult() {
        when(orderRepo.findByAccessionNumber("ACC001"))
                .thenReturn(Optional.of(TestOrder.builder().id(50L).accessionNumber("ACC001").build()));

        RestResponse<?> response = testResultService.receiveHl7(SAMPLE_HL7);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getError()).isNull();
        verify(orderRepo).save(argThat(o -> o.getStatus() == OrderStatus.COMPLETED));
        verify(orderRepo).save(argThat(o -> o.getTestResult() != null));
        assertThat(response.getData()).isInstanceOf(TestResult.class);
        assertThat(((TestResult) response.getData()).getParameters()).hasSize(2);
    }

    @Test
    void receiveHl7_MissingAccessionNumber_ThrowsException() {
        String invalidHl7 = "MSH|^~\\&|...";

        RestResponse<?> response = testResultService.receiveHl7(invalidHl7);

        assertThat(response.getStatusCode()).isNotEqualTo(200);
        assertThat(response.getError()).isNotNull();
        assertThat(response.getMessage().toString()).contains("Missing accession number");
    }

    @Test
    void receiveHl7_OrderNotFound_ThrowsException() {
        when(orderRepo.findByAccessionNumber("ACC999")).thenReturn(Optional.empty());

        RestResponse<?> response = testResultService.receiveHl7(SAMPLE_HL7.replace("ACC001", "ACC999"));

        assertThat(response.getStatusCode()).isNotEqualTo(200);
        assertThat(response.getError()).isNotNull();
        assertThat(response.getMessage().toString()).contains("TestOrder not found");
    }
}