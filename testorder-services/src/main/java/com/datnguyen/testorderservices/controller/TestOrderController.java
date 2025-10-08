package com.datnguyen.testorderservices.controller;

import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.request.TestOrderUpdateRequest;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderCreationResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderDetail;
import com.datnguyen.testorderservices.dto.response.TestOrderListItem;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.service.TestOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class TestOrderController {

    private final TestOrderService service;



    @GetMapping
    public ResponseEntity<RestResponse<?>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
) {
        int pageIndex = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, size);
        Page<TestOrderCreationResponse> testOrderPage = service.list(status, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("testOrders", testOrderPage.getContent());
        response.put("currentPage", testOrderPage.getNumber() + 1);
        response.put("totalItems", testOrderPage.getTotalElements());
        response.put("totalPages", testOrderPage.getTotalPages());
        RestResponse<?> restResponse = RestResponse.success(response);
        return ResponseEntity.ok(restResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestOrderDetail> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.detail(id));
    }

    @PostMapping
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> create(@Valid @RequestBody TestOrderCreateRequest req) {
        TestOrderCreationResponse response = service.create(req);
        RestResponse<TestOrderCreationResponse> restResponse = RestResponse.success(response);
        return ResponseEntity.ok(restResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> update(@PathVariable Long id,
                                            @Valid @RequestBody TestOrderUpdateRequest req) {
        TestOrderCreationResponse testOrderCreationResponse = service.update(id, req);
        RestResponse<TestOrderCreationResponse> restResponse = RestResponse.success(testOrderCreationResponse);
        return ResponseEntity.ok(restResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "X-User-Id", required = false) Long operatorUserId) {
        Long op = (operatorUserId != null) ? operatorUserId : -1L;
        service.softDelete(id, op);
        return ResponseEntity.noContent().build();
    }
}