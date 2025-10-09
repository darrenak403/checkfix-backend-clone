package com.datnguyen.testorderservices.controller;

import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.request.TestOrderUpdateRequest;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderCreationResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderDetail;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.datnguyen.testorderservices.service.TestOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class TestOrderController {

    private final TestOrderService service;

    @GetMapping
    public ResponseEntity<RestResponse<?>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TestOrderCreationResponse> testOrderPage = service.list(status, PageRequest.of(Math.max(0, page - 1), size));

        return ResponseEntity.ok(RestResponse.success(Map.of(
                "testOrders", testOrderPage.getContent(),
                "currentPage", testOrderPage.getNumber() + 1,
                "totalItems", testOrderPage.getTotalElements(),
                "totalPages", testOrderPage.getTotalPages()
        )));
    }


    @GetMapping("/{id}")
    public ResponseEntity<TestOrderDetail> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.detail(id));
    }


    @PostMapping
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> create(
            @Valid @RequestBody TestOrderCreateRequest req
    ) {
        Long userId = getCurrentUserId(); // lấy userId từ JWT
        log.debug("Creating test order by userId={}", userId);

        // truyền userId vào service để tạo phiếu
        TestOrderCreationResponse response = service.create(req, userId);
        return ResponseEntity.ok(RestResponse.success(response));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TestOrderUpdateRequest req
    ) {
        Long userId = getCurrentUserId();
        log.debug("Updating test order id={} by userId={}", id, userId);

        TestOrderCreationResponse response = service.update(id, req);
        return ResponseEntity.ok(RestResponse.success(response));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        log.debug("Soft-deleting test order id={} by userId={}", id, userId);

        service.softDelete(id, userId);
        RestResponse<?> response = RestResponse.builder()
                .statusCode(200)
                .message("Soft-deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }


    public Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = jwtAuth.getToken();
            Object idClaim = jwt.getClaim("userId");
            if (idClaim != null) {
                return Long.parseLong(idClaim.toString());
            }
        }
        return null;
    }
}

