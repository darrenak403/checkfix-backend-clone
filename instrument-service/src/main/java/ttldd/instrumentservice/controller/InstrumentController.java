package ttldd.instrumentservice.controller;

import ttldd.instrumentservice.dto.request.InstrumentModeChangeRequest;
import ttldd.instrumentservice.dto.response.InstrumentModeChangeResponse;
import ttldd.instrumentservice.service.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instrument")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @PatchMapping("/{id}/mode")
    public ResponseEntity<InstrumentModeChangeResponse> changeInstrumentMode(
            @PathVariable String id,
            @Valid @RequestBody InstrumentModeChangeRequest request) {
        return ResponseEntity.ok(instrumentService.changeInstrumentMode(id, request));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<List<InstrumentModeChangeResponse>> getAuditLogs(@PathVariable String id) {
        return ResponseEntity.ok(instrumentService.getAuditLogs(id));
    }
}