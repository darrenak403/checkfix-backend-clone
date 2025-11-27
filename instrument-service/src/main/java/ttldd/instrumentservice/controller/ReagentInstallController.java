package ttldd.instrumentservice.controller;

import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.request.UpdateReagentStatusRequest;
import ttldd.instrumentservice.dto.response.*;
import ttldd.instrumentservice.service.ReagentService;
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
    private ReagentService reagentService;

    @PostMapping("/install")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF') or hasAnyAuthority('ADD_REAGENTS')")
    public ResponseEntity<?> installReagent(@Valid @RequestBody ReagentInstallRequest reagentInstallRequest) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            ReagentInstallResponse updateInstrument = reagentService.installReagent(reagentInstallRequest);
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

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF') or hasAnyAuthority('ADD_REAGENTS')")
    public ResponseEntity<?> getAllReagents() {
        BaseResponse baseResponse = new BaseResponse();
        try {
            List<ReagentGetAllResponse> reagents = reagentService.getALlReagents();
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

    @PatchMapping("/{reagentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF') or hasAnyAuthority('MODIFY_REAGENTS')")
    public ResponseEntity<?> updateReagentStatus(@Valid @RequestBody UpdateReagentStatusRequest updateReagentStatusRequest, @PathVariable("reagentId") String reagentId) {
        BaseResponse baseResponse = new BaseResponse();

        try {
            UpdateReagentStatusResponse response = reagentService.updateReagentStatus(updateReagentStatusRequest, reagentId);

            baseResponse.setStatus(200);
            baseResponse.setMessage("Reagent status updated successfully");
            baseResponse.setData(response);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to update reagent status: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }
    }

    @PatchMapping("/info/{reagentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF', 'MODIFY_REAGENTS')")
    public ResponseEntity<RestResponse<ReagentInstallResponse>> updateReagentInfo(@Valid @RequestBody ReagentInstallRequest reagentInstallRequest, @PathVariable("reagentId") String reagentId) {

        ReagentInstallResponse response = reagentService.updateReagentInfo(reagentInstallRequest, reagentId);
        RestResponse<ReagentInstallResponse> baseResponse = RestResponse.<ReagentInstallResponse>builder()
                .statusCode(200)
                .message("Reagent info updated successfully")
                .data(response)
                .build();
        return ResponseEntity.ok(baseResponse);
    }

    @DeleteMapping("/{reagentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_MANAGER') or hasAnyAuthority('ROLE_DOCTOR') or hasAnyAuthority('ROLE_STAFF') or hasAnyAuthority('DELETE_REAGENTS')")
    public ResponseEntity<?> deleteReagent(@PathVariable("reagentId") String reagentId) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            ReagentDeleteResponse response = reagentService.deleteReagent(reagentId);
            baseResponse.setStatus(200);
            baseResponse.setMessage("Reagent deleted successfully");
            baseResponse.setData(response);
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setStatus(500);
            baseResponse.setMessage("Failed to delete reagent: " + e.getMessage());
            return ResponseEntity.status(500).body(baseResponse);
        }
    }
}
