package ttldd.testorderservices.controller;

import ttldd.testorderservices.client.PatientDTO;
import ttldd.testorderservices.dto.TestOrderDTO;
import ttldd.testorderservices.dto.request.TestOrderCreateRequest;
import ttldd.testorderservices.dto.request.TestOrderUpdateRequest;
import ttldd.testorderservices.dto.request.TestOrderUpdateStatusRequest;
import ttldd.testorderservices.dto.response.*;
import ttldd.testorderservices.entity.OrderStatus;
import ttldd.testorderservices.service.imp.TestOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<TestOrderDetailResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.detail(id));
    }


    @PostMapping
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> create(
            @Valid @RequestBody TestOrderCreateRequest req
    ) {
        // truyền userId vào service để tạo phiếu
        TestOrderCreationResponse response = service.create(req);
        return ResponseEntity.ok(RestResponse.success(response));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TestOrderUpdateRequest req
    ) {

        TestOrderCreationResponse response = service.update(id, req);
        return ResponseEntity.ok(RestResponse.success(response));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<RestResponse<TestOrderCreationResponse>> approve(@PathVariable Long id, @RequestBody TestOrderUpdateStatusRequest req) {
        TestOrderCreationResponse response = service.updateStatus(id, req);
        return ResponseEntity.ok(RestResponse.success(response));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.softDelete(id);
        RestResponse<?> response = RestResponse.builder()
                .statusCode(200)
                .message("Soft-deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<?>> getOrdersByPatientId(
            @PathVariable Long patientId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        PageResponse<TestOrderResponse> orders = service.getAllOrdersByPatientId(patientId, page, size);
        return ResponseEntity.ok(RestResponse.success(orders));
    }

    @GetMapping("/accessionNumber/patient/{accessionNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_STAFF') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('READ_ONLY')")
    public ResponseEntity<RestResponse<?>> getOrdersByAccessionNumber(@PathVariable String accessionNumber){
        PatientDTO patientDTO = service.getPatientByAccessionNumber(accessionNumber);
        return ResponseEntity.ok(RestResponse.success(patientDTO));
    }

    @GetMapping("/accessionNumber/testOrder/{accessionNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_STAFF') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('READ_ONLY')")
    public ResponseEntity<RestResponse<?>> getTestOrdersByAccessionNumber(@PathVariable String accessionNumber){
        TestOrderDTO testOrderDTO = service.getTestOrderByAccessionNumber(accessionNumber);
        return ResponseEntity.ok(RestResponse.success(testOrderDTO));
    }

}

