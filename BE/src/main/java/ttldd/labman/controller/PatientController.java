package ttldd.labman.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.service.PatientService;
import ttldd.labman.service.imp.PatientServiceImp;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
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

<<<<<<< HEAD
    @PatchMapping("/{id}")
=======
    @PatchMapping
>>>>>>> a46baebf7f0416d41011ef25bb7d5a36256d5562
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<RestResponse<PatientResponse>> updatePatient(@RequestParam Long id, @RequestBody PatientRequest request) {
        PatientResponse res = patientService.updatePatient(id, request);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(200)
                .message("Patient updated successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }

<<<<<<< HEAD
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<RestResponse<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        RestResponse<Void> response = RestResponse.<Void>builder()
                .statusCode(200)
                .message("Patient deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

=======
>>>>>>> a46baebf7f0416d41011ef25bb7d5a36256d5562
}
