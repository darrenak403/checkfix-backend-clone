package ttldd.instrumentservice.utils;

import ttldd.instrumentservice.client.PatientClient;
import ttldd.instrumentservice.client.TestOrderDTO;
import ttldd.instrumentservice.dto.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class HL7Utils {
    @Autowired
    private PatientClient patientClient;

    public String generateHL7(RestResponse<TestOrderDTO> testOrderDTO, String sampleData) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String messageId = "MSG" + timestamp;

        Map<String, String> indicators = Arrays.stream(sampleData.split(";"))
                .map(String::trim)
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(
                        arr -> arr[0].trim(),
                        arr -> arr[1].trim()
                ));

        StringBuilder hl7 = new StringBuilder();

        // === MSH Segment ===
        hl7.append("MSH|^~\\&|BloodAnalyzer|Lab|LIS|Hospital|")
                .append(timestamp)
                .append("||ORU^R01|")
                .append(messageId)
                .append("|P|2.5.1\r");

        // === PID Segment ===

        String HL7YOB = testOrderDTO.getData().getYob()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String patientCode = Objects.requireNonNull(patientClient.getPatient(testOrderDTO.getData().getPatientId())).getData().getPatientCode();

        hl7.append("PID|1||")
                .append(patientCode)
                .append("||")
                .append(testOrderDTO.getData().getPatientName())
                .append("||")
                .append(HL7YOB)
                .append("|")
                .append(testOrderDTO.getData().getGender())
                .append("\r");

        // === OBR Segment ===
        hl7.append("OBR|1|")
                .append(testOrderDTO.getData().getAccessionNumber())
                .append("|CBC^Complete Blood Count|||").append("||||||||||||")
                .append(testOrderDTO.getData().getInstrumentId()).append("^").append(testOrderDTO.getData().getInstrumentName()).append("||")
                .append(timestamp)
                .append("\r");

        // === OBX Segment Helper ===
        int index = 1;
        appendOBX(hl7, index++, "WBC", "White Blood Cells", indicators.get("WBC"), "10^3/uL", 4.0, 10.0);
        appendOBX(hl7, index++, "LYM%", "Lymphocytes %", indicators.get("LYM%"), "%", 20.0, 40.0);
        appendOBX(hl7, index++, "MON%", "Monocytes %", indicators.get("MON%"), "%", 3.0, 15.0);
        appendOBX(hl7, index++, "NEU%", "Neutrophils %", indicators.get("NEU%"), "%", 50.0, 70.0);
        appendOBX(hl7, index++, "HGB", "Hemoglobin", indicators.get("HGB"), "g/dL", 12.0, 17.0);
        appendOBX(hl7, index++, "RBC", "Red Blood Cells", indicators.get("RBC"), "10^6/uL", 4.0, 5.5);
        appendOBX(hl7, index++, "HCT", "Hematocrit", indicators.get("HCT"), "%", 37.0, 54.0);
        appendOBX(hl7, index++, "MCV", "Mean Corpuscular Volume", indicators.get("MCV"), "fL", 80.0, 100.0);
        appendOBX(hl7, index++, "MCH", "Mean Corpuscular Hemoglobin", indicators.get("MCH"), "pg", 27.0, 34.0);
        appendOBX(hl7, index++, "MCHC", "Mean Corpuscular Hemoglobin Concentration", indicators.get("MCHC"), "g/dL", 32.0, 36.0);
        appendOBX(hl7, index, "PLT", "Platelets", indicators.get("PLT"), "10^9/L", 150.0, 400.0);

        return hl7.toString();
    }
    public String generateBloodIndicators() {
        Random random = new Random();

        double hgb = getRandomValue(random, 12, 17, 11, 18);
        double hct = getRandomValue(random, 37, 54, 30, 60);
        double wbc = getRandomValue(random, 4, 10, 2, 15);
        double rbc = getRandomValue(random, 4, 5.5, 3, 6);
        int plt = (int) getRandomValue(random, 150, 400, 80, 500);
        double mcv = getRandomValue(random, 80, 100, 70, 110);
        double mch = getRandomValue(random, 27, 34, 20, 38);
        double mchc = getRandomValue(random, 32, 36, 30, 38);
        double neu = getRandomValue(random, 50, 70, 30, 80);
        double lym = getRandomValue(random, 20, 40, 10, 50);
        double mon = getRandomValue(random, 3, 15, 1, 20);

        return String.format(
                "HGB=%.1f; HCT=%.1f; WBC=%.1f; RBC=%.1f; PLT=%d; MCV=%.1f; MCH=%.1f; MCHC=%.1f; NEU%%=%.1f; LYM%%=%.1f; MON%%=%.1f",
                hgb, hct, wbc, rbc, plt, mcv, mch, mchc, neu, lym, mon
        );
    }

    private void appendOBX(StringBuilder hl7, int index, String code, String name, String valueStr,
                           String unit, double refMin, double refMax) {
        if (valueStr == null || valueStr.isEmpty()) return;

        double value = Double.parseDouble(valueStr);
        String flag;

        if (value < refMin) flag = "L"; // Low
        else if (value > refMax) flag = "H"; // High
        else flag = "N"; // Normal

        hl7.append(String.format("OBX|%d|NM|%s^%s||%s|%s|%.1f-%.1f|%s|||F\r",
                index, code, name, valueStr, unit, refMin, refMax, flag));
    }

    private double getRandomValue(Random random, double normalMin, double normalMax, double absoluteMin, double absoluteMax) {
        double chance = random.nextDouble();
        if (chance < 0.06) {
            // 6% thấp
            return absoluteMin + random.nextDouble() * (normalMin - absoluteMin);
        } else if (chance < 0.96) {
            // 90% bình thường
            return normalMin + random.nextDouble() * (normalMax - normalMin);
        } else {
            // 4% cao
            return normalMax + random.nextDouble() * (absoluteMax - normalMax);
        }
    }

}
