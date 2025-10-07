package com.datnguyen.testorderservices.service;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class FlaggingConfigLocal {

    // Cấu hình ngưỡng giá trị cho các chỉ số phổ biến
    private final Map<String, Double[]> numericRules = new HashMap<>();

    public FlaggingConfigLocal() {
        // 🔹 Các rule mô phỏng
        numericRules.put("WBC", new Double[]{4.0, 10.0});  // White Blood Cell
        numericRules.put("RBC", new Double[]{3.8, 5.8});   // Red Blood Cell
        numericRules.put("HGB", new Double[]{12.0, 18.0}); // Hemoglobin
        numericRules.put("CD4 COUNT", new Double[]{350.0, 500.0});
        numericRules.put("GLUCOSE", new Double[]{70.0, 140.0});
    }

    /**
     * 🧠 Đánh giá flag dựa vào parameter và value
     * @param parameter: Tên chỉ số (WBC, HGB, HIV Antibody, …)
     * @param valueStr: Giá trị (số hoặc text)
     * @return NORMAL / HIGH / LOW / CRITICAL / INVALID
     */
    public String evaluate(String parameter, String valueStr) {
        if (parameter == null || valueStr == null) return "INVALID";

        String param = parameter.trim().toUpperCase();

        // 1️⃣ Nếu là chỉ số định lượng (số)
        try {
            double value = Double.parseDouble(valueStr);
            Double[] range = numericRules.get(param);
            if (range == null) return "NORMAL"; // Không có rule → mặc định NORMAL

            if (value < range[0]) return "LOW";
            if (value > range[1]) return "HIGH";
            return "NORMAL";
        }
        catch (NumberFormatException e) {
            // 2️⃣ Nếu là text (Positive / Negative / Reactive)
            String val = valueStr.trim().toUpperCase();
            if (val.equals("POSITIVE") || val.equals("REACTIVE"))
                return "CRITICAL";
            if (val.equals("NEGATIVE") || val.equals("NON-REACTIVE"))
                return "NORMAL";
            return "UNKNOWN";
        }
    }
}
