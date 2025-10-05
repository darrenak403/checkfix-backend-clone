//package com.datnguyen.testorderservices.controller;
//
//import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
//import com.datnguyen.testorderservices.dto.request.TestOrderUpdateRequest;
//import com.datnguyen.testorderservices.dto.response.TestOrderDetail;
//import com.datnguyen.testorderservices.dto.response.TestOrderListItem;
//import com.datnguyen.testorderservices.entity.OrderStatus;
//import com.datnguyen.testorderservices.entity.TestOrder;
//import com.datnguyen.testorderservices.service.TestOrderService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class TestOrderController {
//
//    private final TestOrderService service;
//
//    @GetMapping
//    public ResponseEntity<Page<TestOrderListItem>> list(
//            @RequestParam(required = false) OrderStatus status,
//            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResponseEntity.ok(service.list(status, pageable));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<TestOrderDetail> detail(@PathVariable Long id) {
//        return ResponseEntity.ok(service.detail(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<TestOrder> create(@Valid @RequestBody TestOrderCreateRequest req) {
//        return ResponseEntity.ok(service.create(req));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<TestOrder> update(@PathVariable Long id,
//                                            @Valid @RequestBody TestOrderUpdateRequest req,
//                                            @RequestHeader(value = "X-User-Id", required = false) Long operatorUserId) {
//        Long op = (operatorUserId != null) ? operatorUserId : -1L;
//        return ResponseEntity.ok(service.update(id, req, op));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id,
//                                       @RequestHeader(value = "X-User-Id", required = false) Long operatorUserId) {
//        Long op = (operatorUserId != null) ? operatorUserId : -1L;
//        service.softDelete(id, op);
//        return ResponseEntity.noContent().build();
//    }
//}