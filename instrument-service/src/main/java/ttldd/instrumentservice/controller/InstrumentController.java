package ttldd.instrumentservice.controller;

import ttldd.instrumentservice.dto.request.ChangeModeRequest;
import ttldd.instrumentservice.dto.response.BaseResponse;
import ttldd.instrumentservice.dto.response.ChangeModeResponse;
import ttldd.instrumentservice.service.InstrumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttldd.instrumentservice.service.imp.RawHl7Service;


@RestController
@RequestMapping
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InstrumentController {
    InstrumentService instrumentService;
    private final RawHl7Service rawHl7Service;


    @PutMapping("/change-mode")
    public ResponseEntity<?> changeInstrumentMode(@RequestBody ChangeModeRequest changeModeRequest) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            ChangeModeResponse updateInstrument =  instrumentService.changeInstrumentMode(changeModeRequest);
            baseResponse.setStatus(200);
            baseResponse.setMessage("Instrument mode changed successfully");
            baseResponse.setData(updateInstrument);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to change instrument mode: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }
    }

    @PostMapping("/hl7/raw")
    public void autoDelete() {
        rawHl7Service.autoDelete();
    }

}
