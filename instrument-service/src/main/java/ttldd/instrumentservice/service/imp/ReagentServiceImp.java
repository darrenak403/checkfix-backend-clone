package ttldd.instrumentservice.service.imp;

import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.response.ReagentGetAllResponse;
import ttldd.instrumentservice.dto.response.ReagentInstallResponse;
import ttldd.instrumentservice.entity.*;
import ttldd.instrumentservice.repository.ReagentAuditLogRepo;
import ttldd.instrumentservice.repository.ReagentHistoryRepo;
import ttldd.instrumentservice.repository.ReagentRepo;
import ttldd.instrumentservice.service.ReagentService;
import ttldd.instrumentservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
//hello
@Service
public class ReagentServiceImp implements ReagentService {
    @Autowired
    private ReagentRepo reagentRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ReagentHistoryRepo reagentHistoryRepo;

    @Autowired
    private ReagentAuditLogRepo reagentAuditLogRepo;

    @Override
    public ReagentInstallResponse installReagent(ReagentInstallRequest reagentInstallRequest) {
        //Kiểm tra trùng lô
        reagentRepo.findByLotNumber(reagentInstallRequest.getLotNumber()).ifPresent(reagentEntity -> {
            throw new RuntimeException("Reagent with lot number " + reagentInstallRequest.getLotNumber() + " already exists for another reagent.");
        });

        //Kiểm tra hạn dùng
        if (reagentInstallRequest.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expiry date must be in the future.");
        }
        ReagentEntity reagentEntity = ReagentEntity.builder()
                .id(UUID.randomUUID().toString())
                .reagentType(reagentInstallRequest.getReagentType())
                .reagentName(reagentInstallRequest.getReagentName())
                .lotNumber(reagentInstallRequest.getLotNumber())
                .quantity(reagentInstallRequest.getQuantity())
                .unit(reagentInstallRequest.getUnit())
                .expiryDate(reagentInstallRequest.getExpiryDate())
                .vendorId(reagentInstallRequest.getVendorId())
                .vendorName(reagentInstallRequest.getVendorName())
                .vendorContact(reagentInstallRequest.getVendorContact() == null ? "" : reagentInstallRequest.getVendorContact())
                .installedBy(jwtUtils.getFullName())
                .installDate(LocalDate.now())
                .status(ReagentStatus.AVAILABLE)
                .remarks(reagentInstallRequest.getRemarks())
                .build();

        reagentEntity = reagentRepo.save(reagentEntity);

        //ghi lịch sử

        ReagentHistoryEntity reagentHistoryEntity = ReagentHistoryEntity.builder()
                .id(UUID.randomUUID().toString())
                .reagentId(reagentEntity.getId())
                .reagentName(reagentEntity.getReagentName())
                .lotNumber(reagentEntity.getLotNumber())
                .quantity(reagentEntity.getQuantity())
                .unit(reagentEntity.getUnit())
                .expiryDate(reagentEntity.getExpiryDate())
                .vendorId(reagentEntity.getVendorId())
                .vendorName(reagentEntity.getVendorName())
                .installedBy(reagentEntity.getInstalledBy())
                .installTimestamp(LocalDateTime.now())
                .action(reagentEntity.getStatus().toString())
                .remarks(reagentEntity.getRemarks())
                .build();

        reagentHistoryRepo.save(reagentHistoryEntity);

        //ghi auditlog

        ReagentAuditLogEntity reagentAuditLogEntity = ReagentAuditLogEntity.builder()
                .id(UUID.randomUUID().toString())
                .reagentId(reagentEntity.getId())
                .action("INSTALL")
                .username(reagentEntity.getInstalledBy())
                .timestamp(LocalDateTime.now())
                .description(String.format(
                        "User %s installed reagent %s (Lot %s, Qty %d)",
                        reagentEntity.getInstalledBy(),
                        reagentEntity.getReagentName(),
                        reagentEntity.getLotNumber(),
                        reagentEntity.getQuantity()
                ))
                .build();

        reagentAuditLogRepo.save(reagentAuditLogEntity);

        return ReagentInstallResponse.builder()
                .reagentId(reagentEntity.getId())
                .reagentType(reagentEntity.getReagentType())
                .reagentName(reagentEntity.getReagentName())
                .lotNumber(reagentEntity.getLotNumber())
                .quantity(reagentEntity.getQuantity())
                .unit(reagentEntity.getUnit())
                .expiryDate(reagentEntity.getExpiryDate())
                .vendorId(reagentEntity.getVendorId())
                .vendorName(reagentEntity.getVendorName())
                .installedBy(reagentEntity.getInstalledBy())
                .installDate(reagentEntity.getInstallDate())
                .status(reagentEntity.getStatus().toString())
                .build();
    }

    @Override
    public List<ReagentGetAllResponse> getALlReagents() {
        List<ReagentEntity> reagentEntity = reagentRepo.findByStatus(ReagentStatus.AVAILABLE);
        //convert list
        List<ReagentGetAllResponse> reagentGetAllResponses = reagentEntity.stream()
                .map(this::convertToReagentGetAllResponse)
                .toList();
        return  reagentGetAllResponses;
    }

    private ReagentGetAllResponse convertToReagentGetAllResponse(ReagentEntity reagentEntity) {
        return ReagentGetAllResponse.builder()
                .id(reagentEntity.getId())
                .reagentType(reagentEntity.getReagentType())
                .reagentName(reagentEntity.getReagentName())
                .lotNumber(reagentEntity.getLotNumber())
                .quantity(reagentEntity.getQuantity())
                .unit(reagentEntity.getUnit())
                .expiryDate(reagentEntity.getExpiryDate())
                .vendorId(reagentEntity.getVendorId())
                .vendorName(reagentEntity.getVendorName())
                .installedBy(reagentEntity.getInstalledBy())
                .installDate(reagentEntity.getInstallDate())
                .status(reagentEntity.getStatus())
                .remarks(reagentEntity.getRemarks())
                .build();
    }
}
