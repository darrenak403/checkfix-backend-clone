package ttldd.labman.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;
import ttldd.labman.dto.response.RestResponse;
import ttldd.labman.service.PatientService;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<RestResponse<PatientResponse>> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse res = patientService.createPatient(request);
        RestResponse<PatientResponse> response = RestResponse.<PatientResponse>builder()
                .statusCode(201)
                .message("Patient created successfully")
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }


}
