package com.datnguyen.testorderservices.service;

import com.datnguyen.testorderservices.dto.request.TestResultCreateRequest;
import com.datnguyen.testorderservices.entity.HistoryOrderAudit;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.entity.TestResult;
import com.datnguyen.testorderservices.repository.HistoryOrderAuditRepository;
import com.datnguyen.testorderservices.repository.TestOrderRepository;
import com.datnguyen.testorderservices.repository.TestResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class Hl7ResultProcessor {

        private final Hl7SimpleParser parser;
        private final FlaggingConfigLocal flagging;
        private final TestOrderRepository orderRepo;
        private final TestResultRepository resultRepo;
        private final HistoryOrderAuditRepository auditRepo;

        private final ObjectMapper om = new ObjectMapper().findAndRegisterModules();

        @Transactional
        public void addTestResults(Long orderId, String hl7Raw) {
            // 1️⃣ Kiểm tra order tồn tại
            TestOrder order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

            // 2️⃣ Parse HL7 text
            var parsed = parser.parse(hl7Raw);

            // 3️⃣ Biến đếm flag summary
            Map<String, AtomicInteger> flagCount = new HashMap<>();

            // 4️⃣ Lưu từng chỉ số
            parsed.getResults().forEach(r -> {
                String flag = flagging.evaluate(r.getParameter(), r.getValue());

                // đếm flag
                flagCount.computeIfAbsent(flag, k -> new AtomicInteger(0)).incrementAndGet();

                TestResult entity = TestResult.builder()
                        .order(order)
                        .parameter(r.getParameter())
                        .value(r.getValue())
                        .flag(flag)
                        .hl7Raw(hl7Raw)
                        .build();
                resultRepo.save(entity);
            });

            // 5️⃣ Cập nhật trạng thái phiếu
            order.setStatus(OrderStatus.COMPLETED);
            order.setRunAt(LocalDateTime.now());
            orderRepo.save(order);

            // 6️⃣ Ghi log audit
            try {
                Map<String, Object> detail = new HashMap<>();
                detail.put("totalResults", parsed.getResults().size());
                detail.put("flagSummary", flagCount);
                String jsonDetail = om.writeValueAsString(detail);

                auditRepo.save(HistoryOrderAudit.builder()
                        .orderId(orderId)
                        .action("ADD_RESULTS")
                        .detail(jsonDetail)
                        .operatorUserId(-1L)  // hệ thống tự động
                        .build());
            } catch (Exception e) {
                System.err.println("⚠️ Không ghi được audit log: " + e.getMessage());
            }


        }
    @Transactional
    public void addManualResults(Long orderId, TestResultCreateRequest request) {
        TestOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

        Map<String, AtomicInteger> flagCount = new HashMap<>();

        for (var r : request.getResults()) {
            String flag = flagging.evaluate(r.getParameter(), r.getValue());
            flagCount.computeIfAbsent(flag, k -> new AtomicInteger(0)).incrementAndGet();

            TestResult entity = TestResult.builder()
                    .order(order)
                    .parameter(r.getParameter())
                    .value(r.getValue())
                    .flag(flag)
                    .hl7Raw(request.getHl7Raw())
                    .createdAt(LocalDateTime.now())
                    .build();
            resultRepo.save(entity);
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setRunAt(LocalDateTime.now());
        orderRepo.save(order);

        try {
            Map<String, Object> detail = new HashMap<>();
            detail.put("totalResults", request.getResults().size());
            detail.put("flagSummary", flagCount);
            String jsonDetail = om.writeValueAsString(detail);

            auditRepo.save(HistoryOrderAudit.builder()
                    .orderId(orderId)
                    .action("ADD_MANUAL_RESULTS")
                    .detail(jsonDetail)
                    .operatorUserId(-1L)
                    .createdAt(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            System.err.println("⚠️ Không ghi được audit log: " + e.getMessage());
        }

}
}


