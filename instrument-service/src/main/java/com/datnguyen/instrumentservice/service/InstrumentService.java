package com.datnguyen.instrumentservice.service;

import com.datnguyen.instrumentservice.dto.request.InstrumentModeChangeRequest;
import com.datnguyen.instrumentservice.dto.response.InstrumentModeChangeResponse;
import com.datnguyen.instrumentservice.entity.Instrument;
import com.datnguyen.instrumentservice.entity.InstrumentModeAudit;
import com.datnguyen.instrumentservice.exception.ResourceNotFoundException;
import com.datnguyen.instrumentservice.repository.InstrumentModeAuditRepository;
import com.datnguyen.instrumentservice.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentModeAuditRepository auditRepository;




    // ✅ Thay đổi trạng thái máy


    public InstrumentModeChangeResponse changeInstrumentMode(String id, InstrumentModeChangeRequest req) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrument not found: " + id));

        String oldMode = instrument.getMode();
        String newMode = req.getNewMode();

        // --- Validate theo yêu cầu nghiệp vụ ---


        if (newMode.equalsIgnoreCase("Maintenance") || newMode.equalsIgnoreCase("Inactive")) {
            if (req.getReason() == null || req.getReason().isBlank()) {
                throw new IllegalArgumentException("Reason is required for Maintenance or Inactive mode.");
            }
        }

        if (newMode.equalsIgnoreCase("Ready")) {
            if (req.getQcPassed() == null || !req.getQcPassed()) {
                throw new IllegalArgumentException("QC check must be passed before setting Ready mode.");
            }
            instrument.setLastQcPassed(true);
        }

        // --- Cập nhật trạng thái ---
        instrument.setMode(newMode);
        instrument.setReason(req.getReason());
        instrument.setLastChangedBy(req.getChangedBy());
        instrument.setLastChangedAt(LocalDateTime.now());
        instrumentRepository.save(instrument);

        // --- Ghi lịch sử ---
        InstrumentModeAudit audit = InstrumentModeAudit.builder()
                .instrumentId(id)
                .previousMode(oldMode)
                .newMode(newMode)
                .reason(req.getReason())
                .changedBy(req.getChangedBy())
                .changedAt(LocalDateTime.now())
                .build();
        auditRepository.save(audit);

        log.info("Instrument [{}] changed from [{}] → [{}] by [{}]",
                id, oldMode, newMode, req.getChangedBy());

        // --- Trả phản hồi ---
        return InstrumentModeChangeResponse.builder()
                .instrumentId(id)
                .oldMode(oldMode)
                .newMode(newMode)
                .reason(req.getReason())
                .changedBy(req.getChangedBy())
                .changedAt(LocalDateTime.now())
                .build();
    }

    // ✅ Lấy danh sách log thay đổi mode
    public List<InstrumentModeChangeResponse> getAuditLogs(String instrumentId) {
        return auditRepository.findByInstrumentIdOrderByChangedAtDesc(instrumentId)
                .stream()
                .map(a -> InstrumentModeChangeResponse.builder()
                        .instrumentId(a.getInstrumentId())
                        .oldMode(a.getPreviousMode())
                        .newMode(a.getNewMode())
                        .reason(a.getReason())
                        .changedBy(a.getChangedBy())
                        .changedAt(a.getChangedAt())
                        .build())
                .toList();
    }
}