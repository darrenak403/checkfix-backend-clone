package com.datnguyen.instrumentservice.service.imp;

import com.datnguyen.instrumentservice.dto.request.ReagentInstallRequest;
import com.datnguyen.instrumentservice.dto.response.ReagentInstallResponse;
import com.datnguyen.instrumentservice.entity.ReagentAuditLogEntity;
import com.datnguyen.instrumentservice.entity.ReagentEntity;
import com.datnguyen.instrumentservice.entity.ReagentHistoryEntity;
import com.datnguyen.instrumentservice.entity.ReagentStatus;
import com.datnguyen.instrumentservice.repository.ReagentAuditLogRepo;
import com.datnguyen.instrumentservice.repository.ReagentHistoryRepo;
import com.datnguyen.instrumentservice.repository.ReagentRepo;
import com.datnguyen.instrumentservice.service.ReagentService;
import com.datnguyen.instrumentservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .instrumentId(reagentInstallRequest.getInstrumentId())
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
}
