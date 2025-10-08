package jungle.warehouseservice.controller;

import jakarta.validation.Valid;
import jungle.warehouseservice.dto.request.InstrumentRequest;
import jungle.warehouseservice.dto.response.InstrumentResponse;
import jungle.warehouseservice.dto.response.RestResponse;
import jungle.warehouseservice.service.InstrumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        RestResponse<InstrumentResponse> restResponse =  RestResponse.<InstrumentResponse>builder()
                .statusCode(201)
                .data(ins)
                .message("Instrument created successfully")
                .build();
        return ResponseEntity.status(201).body(restResponse);
    }

}
