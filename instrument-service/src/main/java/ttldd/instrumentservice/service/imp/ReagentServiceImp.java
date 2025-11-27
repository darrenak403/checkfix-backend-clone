package ttldd.instrumentservice.service.imp;

import org.springframework.util.StringUtils;
import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.request.UpdateReagentStatusRequest;
import ttldd.instrumentservice.dto.response.ReagentDeleteResponse;
import ttldd.instrumentservice.dto.response.ReagentGetAllResponse;
import ttldd.instrumentservice.dto.response.ReagentInstallResponse;
import ttldd.instrumentservice.dto.response.UpdateReagentStatusResponse;
import ttldd.instrumentservice.entity.*;
import ttldd.instrumentservice.repository.ReagentAuditLogRepo;
import ttldd.instrumentservice.repository.ReagentHistoryRepo;
import ttldd.instrumentservice.repository.ReagentRepo;
import ttldd.instrumentservice.repository.ReagentUpdateAuditLogRepo;
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

    @Autowired
    private ReagentUpdateAuditLogRepo reagentUpdateAuditLogRepo;

    @Override
    public ReagentInstallResponse installReagent(ReagentInstallRequest reagentInstallRequest) {
        //Kiểm tra trùng lô
        reagentRepo.findByLotNumberAndDeletedFalse(reagentInstallRequest.getLotNumber()).ifPresent(reagentEntity -> {
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
        List<ReagentEntity> reagentEntity = reagentRepo.findByDeletedFalse();

        //convert list
        return reagentEntity.stream()
                .map(this::convertToReagentGetAllResponse)
                .toList();
    }

    @Override
    public UpdateReagentStatusResponse updateReagentStatus(UpdateReagentStatusRequest updateReagentRequest, String reagentId) {
        ReagentEntity reagentEntity = reagentRepo.findByIdAndDeletedFalse(reagentId).orElseThrow(() -> new RuntimeException("Reagent with ID " + reagentId + " not found."));

        ReagentStatus currentStatus = reagentEntity.getStatus();

//        if (reagentEntity.getStatus() == updateReagentRequest.getReagentStatus()) {
//            throw new RuntimeException("Reagent " + reagentEntity.getReagentName() + " is already in status " + updateReagentRequest.getReagentStatus());
//        }
        if (updateReagentRequest.getQuantity() < 5 && updateReagentRequest.getReagentStatus() == ReagentStatus.AVAILABLE) {
            throw new RuntimeException("Quantity must be at least 5 to set status to AVAILABLE.");
        }
        if(StringUtils.hasText(updateReagentRequest.getReagentStatus().toString())){
            reagentEntity.setStatus(updateReagentRequest.getReagentStatus());
        }
        reagentEntity.setQuantity(updateReagentRequest.getQuantity());
        reagentRepo.save(reagentEntity);

        //ghi lịch sử

        ReagentUpdateAuditLogEntity reagentUpdateAuditLogEntity = ReagentUpdateAuditLogEntity.builder()
                .id(UUID.randomUUID().toString())
                .reagentName(reagentEntity.getReagentName())
                .updatedBy(jwtUtils.getFullName())
                .oldStatus(reagentEntity.getStatus())
                .newStatus(updateReagentRequest.getReagentStatus())
                .oldValue(reagentEntity.getQuantity())
                .newValue(updateReagentRequest.getQuantity())
                .action("UPDATE")
                .timestamp(LocalDateTime.now())
                .build();
        reagentUpdateAuditLogRepo.save(reagentUpdateAuditLogEntity);

        return UpdateReagentStatusResponse.builder()
                .reagentName(reagentEntity.getReagentName())
                .oldStatus(currentStatus)
                .newStatus(updateReagentRequest.getReagentStatus())
                .oldQuantity(reagentEntity.getQuantity())
                .newQuantity(updateReagentRequest.getQuantity())
                .timestamp(LocalDateTime.now())
                .action(reagentUpdateAuditLogEntity.getAction())
                .updatedBy(jwtUtils.getFullName())
                .build();
    }

    @Override
    public ReagentInstallResponse updateReagentInfo(ReagentInstallRequest reagentInstallRequest, String reagentId) {
        ReagentEntity reagentEntity = reagentRepo.findByIdAndDeletedFalse(reagentId).orElseThrow(() -> new RuntimeException("Reagent with ID " + reagentId + " not found."));
        if (StringUtils.hasText(reagentInstallRequest.getReagentName())) {
            reagentEntity.setReagentName(reagentInstallRequest.getReagentName());
        }
        if (reagentInstallRequest.getReagentType() != null) {
            reagentEntity.setReagentType(reagentInstallRequest.getReagentType());
        }
        reagentEntity.setQuantity(reagentInstallRequest.getQuantity());
        if (StringUtils.hasText(reagentInstallRequest.getUnit())) {
            reagentEntity.setUnit(reagentInstallRequest.getUnit());
        }
        if (reagentInstallRequest.getExpiryDate() != null) {
            reagentEntity.setExpiryDate(reagentInstallRequest.getExpiryDate());
        }
        if (StringUtils.hasText(reagentInstallRequest.getVendorId())) {
            reagentEntity.setVendorId(reagentInstallRequest.getVendorId());
        }
        if (StringUtils.hasText(reagentInstallRequest.getVendorName())) {
            reagentEntity.setVendorName(reagentInstallRequest.getVendorName());
        }
        if (StringUtils.hasText(reagentInstallRequest.getVendorContact())) {
            reagentEntity.setVendorContact(reagentInstallRequest.getVendorContact());
        }
        if (StringUtils.hasText(reagentInstallRequest.getRemarks())) {
            reagentEntity.setRemarks(reagentInstallRequest.getRemarks());
        }
        reagentRepo.save(reagentEntity);
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
                .status(reagentEntity.getStatus() != null ? reagentEntity.getStatus().toString() : null)
                .build();
    }

    @Override
    public ReagentDeleteResponse deleteReagent(String reagentId) {
        ReagentEntity reagentEntity = reagentRepo.findByIdAndDeletedFalse(reagentId).orElseThrow(() -> new RuntimeException("Reagent with ID " + reagentId + " not found."));
        reagentEntity.setDeleted(true);
        reagentRepo.save(reagentEntity);

        //ghi log
        ReagentUpdateAuditLogEntity reagentUpdateAuditLogEntity = ReagentUpdateAuditLogEntity.builder()
                .id(UUID.randomUUID().toString())
                .reagentName(reagentEntity.getReagentName())
                .updatedBy(jwtUtils.getFullName())
                .oldStatus(reagentEntity.getStatus())
                .newStatus(reagentEntity.getStatus())
                .oldValue(reagentEntity.getQuantity())
                .newValue(reagentEntity.getQuantity())
                .action("DELETE")
                .timestamp(LocalDateTime.now())
                .build();
        reagentUpdateAuditLogRepo.save(reagentUpdateAuditLogEntity);

        return ReagentDeleteResponse.builder()
                .reagentId(reagentEntity.getId())
                .reagentName(reagentEntity.getReagentName())
                .action("DELETED")
                .lotNumber(reagentEntity.getLotNumber())
                .deletedBy(jwtUtils.getFullName())
                .deletedAt(LocalDateTime.now())
                .build();
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
