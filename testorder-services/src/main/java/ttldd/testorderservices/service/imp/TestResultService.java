package ttldd.testorderservices.service.imp;

import ttldd.testorderservices.dto.response.RestResponse;
import ttldd.testorderservices.dto.response.TestResultResponse;
import ttldd.testorderservices.entity.OrderStatus;
import ttldd.testorderservices.entity.TestOrder;
import ttldd.testorderservices.entity.TestResult;
import ttldd.testorderservices.entity.TestResultParameter;
import ttldd.testorderservices.mapper.TestResultMapper;
import ttldd.testorderservices.producer.NotificationProducer;
import ttldd.testorderservices.repository.TestOrderRepository;
import ttldd.testorderservices.repository.TestResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttldd.testorderservices.util.CryptoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestResultService {

    private final TestOrderRepository orderRepo;
    private final TestResultRepository resultRepo;

    private final TestResultMapper mapper;
    private final NotificationProducer notificationProducer;
    private final CryptoUtil cryptoUtil;


    @Transactional
    public RestResponse<?> getResultByAccession(String accession) {
        var result = resultRepo.findByAccessionNumber(accession)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kết quả cho accession: " + accession));

        TestResultResponse dto = mapper.toDto(result);
        return RestResponse.success("Lấy kết quả thành công", dto);
    }


//    @Transactional
    public RestResponse<?> receiveHl7(String rawHl7) {
        try {
            String hl7Formatted = formatHL7(rawHl7);
            if (hl7Formatted.isBlank()) {
                throw new IllegalArgumentException("HL7 message is empty");
            }


            String[] lines = hl7Formatted.split("\\r?\\n");
            String accession = null;
            List<TestResultParameter> params = new ArrayList<>();

            for (String line : lines) {
                if (line == null || line.isBlank()) continue;
                String[] parts = line.split("\\|");
                if (parts.length == 0) continue;


                if (parts[0].equals("OBR")) {
                    accession = parts.length > 2 && !parts[2].isBlank() ? parts[2] :
                            (parts.length > 3 ? parts[3] : null);
                    log.info("Found accession number: {}", accession);
                }


                else if (parts[0].equals("OBX")) {
                    if (parts.length < 6) continue;

                    TestResultParameter p = new TestResultParameter();
                    p.setSequence(safeInt(parts, 1));
                    p.setObxIdentifier(parts.length > 3 ? parts[3] : "UNKNOWN");

                    // OBX-3 = "WBC^White Blood Cell"
                    String[] idSplit = parts[3].split("\\^");
                    p.setParamCode(idSplit[0]);
                    p.setParamName(idSplit.length > 1 ? idSplit[1] : idSplit[0]);

                    p.setValue(parts.length > 5 ? parts[5] : null);
                    p.setUnit(parts.length > 6 ? parts[6] : null);
                    p.setRefRange(parts.length > 7 ? parts[7] : null);
                    p.setFlag(parts.length > 8 ? parts[8] : "N");
                    p.setComputedBy("HL7 Parser v2.0");

                    params.add(p);
                }
            }

            if (accession == null) {
                throw new IllegalArgumentException(" Missing accession number in HL7 message");
            }


            final String finalAccession = accession.trim();
            log.info(" Finding TestOrder by accession: {}", finalAccession);

            TestOrder order = orderRepo.findByAccessionNumber(finalAccession)
                    .orElseThrow(() -> new IllegalArgumentException(
                            " TestOrder not found for accession: " + finalAccession));


            TestResult result = TestResult.builder()
                    .testOrder(order)
                    .patientId(order.getPatientId())
                    .accessionNumber(order.getAccessionNumber())
                    .instrumentName(order.getInstrumentName())
                    .parseHl7(rawHl7)
                    .status("COMPLETE")
                    .build();

            for (TestResultParameter p : params) {
                p.setTestResult(result);
                p.setTestOrder(order);
            }
            result.setParameters(params);

            order.setTestResult(result);
            order.setStatus(OrderStatus.COMPLETED);
            orderRepo.save(order);
            //send mail test result notification

            String encryptedAccession = cryptoUtil.encryptForURL(order.getAccessionNumber());
            String encryptedOrderId = cryptoUtil.encryptForURL(String.valueOf(order.getId()));
            String resultLink = "http://localhost:3000/service/my-medical-records/"
                    + encryptedOrderId
                    + "/"
                    + encryptedAccession;

            String testName = params.isEmpty() ? "Xét nghiệm" : params.getFirst().getParamName();
            String safeFullName = (order.getPatientName() == null || order.getPatientName().trim().isEmpty())
                    ? "Bạn"
                    : order.getPatientName();
            notificationProducer.sendEmail(
                    "send-email",
                    order.getEmail(),
                    "Kết quả xét nghiệm của bạn đã sẵn sàng",
                    "TEST_RESULT_NOTIFICATION",
                    Map.of(
                            "userName", safeFullName,
                            "accession", order.getAccessionNumber(),
                            "patientName", safeFullName,
                            "testName", testName,
                            "completedDate", LocalDate.now().toString(),
                            "resultLink", resultLink
                    )
            );

            log.info("send notification email to {}", order.getEmail());

            log.info("HL7 parsed successfully for accession {} ({} params)", finalAccession, params.size());
            return RestResponse.success("HL7 parsed successfully", result);

        } catch (Exception e) {
            log.error(" HL7 parse error: {}", e.getMessage(), e);
            return RestResponse.error(400, "ParseError", "HL7 parse failed: " + e.getMessage());
        }
    }

    private String formatHL7(String rawHl7) {
        if (rawHl7 == null || rawHl7.isBlank()) {
            return "";
        }

        String formatted = rawHl7;

        formatted = formatted.replaceAll("\\\\\\\\&", "\\\\&");
        formatted = formatted.replaceAll("\\\\\\&", "\\&");

        formatted = formatted
                .replace("\\r\\n", "\n")
                .replace("\\r", "\n")
                .replace("\\n", "\n")
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        formatted = formatted.trim();

        formatted = formatted.replaceAll("\n+", "\n");

        return formatted;
    }

    private int safeInt(String[] parts, int index) {
        try {
            return Integer.parseInt(parts[index]);
        } catch (Exception e) {
            return 0;
        }
    }
}
