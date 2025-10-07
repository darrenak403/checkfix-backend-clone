package com.datnguyen.testorderservices.service;

import lombok.*;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class Hl7SimpleParser {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        private String parameter;  // Tên chỉ số xét nghiệm
        private String value;      // Giá trị kết quả
        private String flag;       // Tự động thêm sau nếu có FlaggingConfigLocal
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HL7ParsedData {
        private String barcode;            // Mã mẫu / OBR ID
        private List<ResultItem> results;  // Danh sách các kết quả
    }

//
//      📥 Parse HL7 raw string thành object HL7ParsedData
//     Ví dụ chuỗi HL7:
//    OBR|1|12345|67890|CBC_TEST
//    OBX|1|NM|WBC^White Blood Cell||5.2|10^9/L|N|
//    OBX|2|NM|RBC^Red Blood Cell||3.5|10^12/L|L|
    public HL7ParsedData parse(String hl7Raw) {
        String[] lines = hl7Raw.split("\\r?\\n"); // hỗ trợ cả \n và \r\n
        String barcode = null;
        List<ResultItem> results = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("OBR|")) {
                // Ví dụ: OBR|1|12345|67890|CBC_TEST
                String[] parts = line.split("\\|");
                barcode = parts.length > 3 ? parts[3] : "UNKNOWN";
            } else if (line.startsWith("OBX|")) {
                // Ví dụ: OBX|1|NM|WBC^White Blood Cell||5.2|10^9/L|N|
                String[] parts = line.split("\\|");
                String paramSegment = parts.length > 3 ? parts[3] : "UNKNOWN";
                String value = parts.length > 5 ? parts[5] : "0";

                // Nếu param chứa dấu ^ thì lấy phần trước (WBC)
                String parameter = paramSegment.contains("^")
                        ? paramSegment.split("\\^")[0].trim()
                        : paramSegment.trim();

                results.add(ResultItem.builder()
                        .parameter(parameter)
                        .value(value.trim())
                        .build());
            }
        }

        return HL7ParsedData.builder()
                .barcode(barcode)
                .results(results)
                .build();
    }
}
