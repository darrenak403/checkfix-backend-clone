package ttldd.warehouseservice.controller;

import jakarta.validation.Valid;
import ttldd.warehouseservice.dto.request.InstrumentRequest;
import ttldd.warehouseservice.dto.request.InstrumentUpdateRequest;
import ttldd.warehouseservice.dto.response.InstrumentResponse;
import ttldd.warehouseservice.dto.response.InstrumentUpdateResponse;
import ttldd.warehouseservice.dto.response.PageResponse;
import ttldd.warehouseservice.dto.response.RestResponse;
import ttldd.warehouseservice.service.InstrumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instruments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InstrumentController {
    InstrumentService instrumentService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<InstrumentResponse>> createInstruments(@Valid @RequestBody InstrumentRequest instrumentRequest) {
        InstrumentResponse ins = instrumentService.createInstrument(instrumentRequest);
        RestResponse<InstrumentResponse> restResponse = RestResponse.<InstrumentResponse>builder()
                .statusCode(201)
                .data(ins)
                .message("Instrument created successfully")
                .build();
        return ResponseEntity.status(201).body(restResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<PageResponse<InstrumentResponse>>> getAllInstruments(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<InstrumentResponse> instruments = instrumentService.getInstruments(page, size);
        RestResponse<PageResponse<InstrumentResponse>> restResponse = RestResponse.<PageResponse<InstrumentResponse>>builder()
                .statusCode(200)
                .data(instruments)
                .message("Instruments retrieved successfully")
                .build();
        return ResponseEntity.ok(restResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<InstrumentResponse>> getInstrumentById(@PathVariable Long id) {
        InstrumentResponse instrument = instrumentService.getInstrumentById(id);
        RestResponse<InstrumentResponse> restResponse = RestResponse.<InstrumentResponse>builder()
                .statusCode(200)
                .data(instrument)
                .message("Instrument retrieved successfully")
                .build();
        return ResponseEntity.ok(restResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<RestResponse<InstrumentUpdateResponse>> updateInstrument(
            @PathVariable Long id,
            @RequestBody InstrumentUpdateRequest instrumentUpdateRequest) {
        InstrumentUpdateResponse updatedInstrument = instrumentService.updateInstrument(id, instrumentUpdateRequest);
        RestResponse<InstrumentUpdateResponse> restResponse = RestResponse.<InstrumentUpdateResponse>builder()
                .statusCode(200)
                .data(updatedInstrument)
                .message("Instrument updated successfully")
                .build();
        return ResponseEntity.ok(restResponse);
    }
}
