package com.datnguyen.testorderservices.controller;


import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.service.imp.TestResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class TestResultController {

    private final TestResultService resultService;

    @PostMapping(value = "/hl7", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<RestResponse<?>> receiveHl7(@RequestBody String rawHl7) {
        return ResponseEntity.ok(resultService.receiveHl7(rawHl7));
    }
    @GetMapping("/{accession}")
    public ResponseEntity<RestResponse<?>> getResultByAccession(@PathVariable String accession) {
        return ResponseEntity.ok(resultService.getResultByAccession(accession));
    }

}