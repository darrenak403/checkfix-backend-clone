package jungle.patientservice.controller;

import jakarta.validation.Valid;
import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.request.PatientUpdateRequest;
import jungle.patientservice.dto.response.PageResponse;
import jungle.patientservice.dto.response.PatientResponse;
import jungle.patientservice.dto.response.RestResponse;
import jungle.patientservice.dto.response.TestOrderResponse;
import jungle.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<PatientResponse>> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse res = patientService.createPatient(request);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(201)
                .message("Patient created successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<PageResponse<PatientResponse>>> getPatients(@RequestParam (value = "page", defaultValue = "1") int page,
                                                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<PatientResponse> patients = patientService.getPatients(page, size);
        RestResponse<PageResponse<PatientResponse>> response = RestResponse.<PageResponse<PatientResponse>>builder()
                .statusCode(200)
                .message("Patients retrieved successfully")
                .data(patients)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<PatientResponse>> getPatient(@PathVariable Long id) {
        PatientResponse res = patientService.getPatient(id);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(200)
                .message("Patient retrieved successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<List<PatientResponse>>> getMyProfiles() {
        List<PatientResponse> res = patientService.getCurrentPatient();
        RestResponse<List<PatientResponse>> response = RestResponse.<List<PatientResponse>>builder()
                .statusCode(200)
                .message("Patient profile retrieved successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/testOrder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<PageResponse<TestOrderResponse>>> getTestOrders(@RequestParam (value = "page", defaultValue = "1") int page,
                                                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<TestOrderResponse> orders = patientService.getTestOrdersByPatientId(page, size);
        RestResponse<PageResponse<TestOrderResponse>> response = RestResponse.<PageResponse<TestOrderResponse>>builder()
                .statusCode(200)
                .message("Patients retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<PatientResponse>> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientUpdateRequest request) {
        PatientResponse res = patientService.updatePatient(id, request);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(200)
                .message("Patient updated successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        RestResponse<Void> response = RestResponse.<Void>builder()
                .statusCode(200)
                .message("Patient deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
