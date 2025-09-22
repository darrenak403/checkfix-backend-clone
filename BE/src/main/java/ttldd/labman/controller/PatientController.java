package ttldd.labman.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttldd.labman.dto.PatientDTO;
import ttldd.labman.service.PatientService;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO request) {
        request = patientService.createPatient(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello, welcome to the Patient Management System!";
    }

}
