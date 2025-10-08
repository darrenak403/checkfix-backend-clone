package com.datnguyen.testorderservices.service;

import com.datnguyen.testorderservices.dto.request.TestResultCreateRequest;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.entity.TestResult;
import com.datnguyen.testorderservices.repository.TestOrderRepository;
import com.datnguyen.testorderservices.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TestResultService {

    private final TestOrderRepository orderRepo;
    private final TestResultRepository resultRepo;

    public void saveResults(Long orderId, TestResultCreateRequest request) {
        TestOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Test Order not found with ID: " + orderId));

        for (TestResultCreateRequest.ResultItem item : request.getResults()) {
            TestResult result = TestResult.builder()
                    .order(order)
                    .parameter(item.getParameter())
                    .value(item.getValue())
                    .flag(item.getFlag())
                    .hl7Raw(request.getHl7Raw())
                    .createdAt(LocalDateTime.now())
                    .build();

            resultRepo.save(result);
        }
    }
}