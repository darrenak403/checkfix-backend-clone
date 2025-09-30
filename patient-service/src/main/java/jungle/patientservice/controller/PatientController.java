package jungle.patientservice.controller;

import jakarta.validation.Valid;
import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.response.PatientResponse;
import jungle.patientservice.dto.response.RestResponse;
import jungle.patientservice.service.PatientService;
import lombok.RequiredArgsConstructor;
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<RestResponse<List<PatientResponse>>> getPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        RestResponse<List<PatientResponse>> response = RestResponse.<List<PatientResponse>>builder()
                .statusCode(200)
                .message("Patients retrieved successfully")
                .data(patients)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<RestResponse<PatientResponse>> getPatient(@PathVariable Long id) {
        PatientResponse res = patientService.getPatient(id);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(200)
                .message("Patient retrieved successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<RestResponse<PatientResponse>> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        PatientResponse res = patientService.updatePatient(id, request);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(200)
                .message("Patient updated successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<RestResponse<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        RestResponse<Void> response = RestResponse.<Void>builder()
                .statusCode(200)
                .message("Patient deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
