package ttldd.instrumentservice.controller;

import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.response.BaseResponse;
import ttldd.instrumentservice.dto.response.ReagentGetAllResponse;
import ttldd.instrumentservice.dto.response.ReagentInstallResponse;
import ttldd.instrumentservice.service.imp.ReagentServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//hello
@RestController
@RequestMapping("/reagents")
public class ReagentInstallController {
    @Autowired
    private ReagentServiceImp reagentServiceImp;

    @PostMapping("/install")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<?> installReagent(@Valid @RequestBody ReagentInstallRequest reagentInstallRequest) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            ReagentInstallResponse updateInstrument =  reagentServiceImp.installReagent(reagentInstallRequest);
            baseResponse.setStatus(200);
            baseResponse.setMessage("Reagent installed successfully");
            baseResponse.setData(updateInstrument);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to install reagent: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }

    }

    @GetMapping("/all" )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF')")
    public ResponseEntity<?> getAllReagents() {
        BaseResponse baseResponse = new BaseResponse();
        try {
            List<ReagentGetAllResponse> reagents =  reagentServiceImp.getALlReagents();
            baseResponse.setStatus(200);
            baseResponse.setMessage("Get all reagents successfully");
            baseResponse.setData(reagents);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to get all reagents: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }
    }
}
