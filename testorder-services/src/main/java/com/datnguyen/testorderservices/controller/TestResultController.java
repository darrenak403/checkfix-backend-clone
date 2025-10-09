package com.datnguyen.testorderservices.controller;

import com.datnguyen.testorderservices.dto.request.TestResultCreateRequest;
import com.datnguyen.testorderservices.service.Hl7ResultProcessor;
import com.datnguyen.testorderservices.service.TestResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class TestResultController {
//
//    private final TestResultService testResultService;
//
//    @PostMapping("/{id}/results")
//    public ResponseEntity<String> addTestResults(
//            @PathVariable Long id,
//            @RequestBody TestResultCreateRequest request
//    ) {
//        testResultService.saveResults(id, request);
//        return ResponseEntity.ok("Test results saved successfully for order " + id);
//    }
//}
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class TestResultController {
//
//    private final Hl7ResultProcessor hl7Processor;
//
//    /**
//     * 📥 Nhận HL7 raw + lưu kết quả test (auto-flag + audit)
//     */
//    @PostMapping("/{id}/results")
//    public ResponseEntity<String> addTestResults(
//            @PathVariable Long id,
//            @RequestBody TestResultCreateRequest request
//    ) {
//        hl7Processor.addTestResults(id, request.getHl7Raw());
//        return ResponseEntity.ok("✅ HL7 results processed & saved for order " + id);

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class TestResultController {

    private final Hl7ResultProcessor hl7Processor;

    @PostMapping("/{id}/results")
    public ResponseEntity<String> addTestResults(
            @PathVariable Long id,
            @RequestBody TestResultCreateRequest request
    ) {
        // Nếu có chuỗi HL7 → xử lý tự động (ưu tiên)
        if (request.getHl7Raw() != null && request.getHl7Raw().contains("OBX")) {
            hl7Processor.addTestResults(id, request.getHl7Raw());
            return ResponseEntity.ok("✅ HL7 results processed & saved for order " + id);
        }

        // Nếu chỉ gửi JSON thủ công → tự flag từng item rồi lưu
        hl7Processor.addManualResults(id, request);
        return ResponseEntity.ok("✅ Manual test results saved & flagged for order " + id);
    }
}


