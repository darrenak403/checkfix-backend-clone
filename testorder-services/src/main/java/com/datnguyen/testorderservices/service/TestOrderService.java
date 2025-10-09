package com.datnguyen.testorderservices.service;

import com.datnguyen.testorderservices.client.PatientClient;
import com.datnguyen.testorderservices.client.PatientDTO;
import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.request.TestOrderUpdateRequest;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderCreationResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderDetail;
import com.datnguyen.testorderservices.entity.*;
import com.datnguyen.testorderservices.mapper.TestOrderMapper;
import com.datnguyen.testorderservices.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestOrderService {

    private final TestOrderRepository orderRepo;
    private final CommentRepository commentRepo;
    private final HistoryOrderAuditRepository auditRepo;
    private final PatientClient patientClient;
    private final TestOrderMapper mapper;
    private final ObjectMapper om = new ObjectMapper().findAndRegisterModules();

    private static Integer ageFrom(LocalDate dob) {
        return (dob == null) ? null : Period.between(dob, LocalDate.now()).getYears();
    }

    @Transactional
    public TestOrderCreationResponse create(TestOrderCreateRequest req, Long createdByUserId) {
        var patientResponse = getPatient(req.getPatientId());

        TestOrder order = TestOrder.builder()
                .patientId(req.getPatientId())
                .patientName(patientResponse.getFullName())
                .email(patientResponse.getEmail())
                .address(patientResponse.getAddress())
                .phone(patientResponse.getPhone())
                .yob(patientResponse.getYob())
                .gender(patientResponse.getGender())
                .status(OrderStatus.PENDING)
                .createdByUserId(createdByUserId)
                .age(ageFrom(patientResponse.getYob()))
                .deleted(false)
                .build();

        TestOrder saved = orderRepo.save(order);
        logAudit(saved.getId(), "CREATE", safeJson(req), createdByUserId);
        return mapper.toTestOrderCreationResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TestOrderCreationResponse> list(OrderStatus status, Pageable pageable) {
        Page<TestOrder> page = (status == null)
                ? orderRepo.findByDeletedFalse(pageable)
                : orderRepo.findByDeletedFalseAndStatus(status, pageable);

        return page.map(o -> {
            var dto = mapper.toTestOrderCreationResponse(o);
            try {
                var patient = getPatient(o.getPatientId());
                dto.setPatientName(patient.getFullName());
                dto.setGender(patient.getGender());
                dto.setPhone(patient.getPhone());
                dto.setYob(patient.getYob());
                dto.setStatus(o.getStatus() == null ? OrderStatus.PENDING : o.getStatus());
                dto.setCreatedAt(o.getCreatedAt());
            } catch (Exception e) {
                log.warn("Không lấy được dữ liệu bệnh nhân ID={}", o.getPatientId());
            }
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public TestOrderDetail detail(Long id) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));

        var dto = TestOrderDetail.builder()
                .id(o.getId())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .patientId(o.getPatientId())
                .createdByUserId(o.getCreatedByUserId())
                .runAt(o.getRunAt())
                .comments(commentRepo.findByUserId(o.getId()))
                .build();

        try {
            var p = getPatient(o.getPatientId());
            dto.setPatientName(p.getFullName());
            dto.setPatientGender(p.getGender());
            dto.setPatientEmail(p.getEmail());
            dto.setPatientAge(ageFrom(p.getYob()));
        } catch (Exception ignored) {}

        return dto;
    }

    @Transactional
    public TestOrderCreationResponse update(Long id, TestOrderUpdateRequest req) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));

        if (StringUtils.hasText(req.getFullName())) o.setPatientName(req.getFullName());
        if (StringUtils.hasText(req.getPhone())) o.setPhone(req.getPhone());
        if (StringUtils.hasText(req.getAddress())) o.setAddress(req.getAddress());
        if (req.getYob() != null) o.setYob(req.getYob());

        orderRepo.save(o);
        return mapper.toTestOrderCreationResponse(o);



    }

    @Transactional
    public void softDelete(Long id, Long operatorUserId) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại hoặc đã xoá"));

        o.setDeleted(true);
        orderRepo.save(o);

        logAudit(o.getId(), "DELETE", safeJson(o), operatorUserId);
    }

    private PatientDTO getPatient(Long patientId) {
        try {
            RestResponse<PatientDTO> response = patientClient.getById(patientId);
            if (response == null || response.getData().isDeleted())
                throw new IllegalArgumentException("Bệnh nhân không tồn tại hoặc đã bị xoá");
            return response.getData();
        } catch (Exception e) {
            throw new IllegalArgumentException("Không kết nối được tới Patient Service");
        }
    }


    // Helper: ghi audit

//    @Transactional(readOnly = true)
//    public Page<TestOrderListItem> list(OrderStatus status, Pageable pageable) {
//        // 1) Lấy danh sách phiếu từ DB
//        Page<TestOrder> page = (status == null)
//                ? orderRepo.findByDeletedFalse(pageable)
//                : orderRepo.findByDeletedFalseAndStatus(status, pageable);
//
//        // 2) Map sang DTO ListItem + enrich Patient info
//        return page.map(o -> {
//            TestOrderListItem item = TestOrderListItem.builder()
//                    .id(o.getId())
//                    .status(o.getStatus())
//                    .createdAt(o.getCreatedAt())
//                    .patientId(o.getPatientId())
//                    .build();
//
//            // Gọi Patient Service enrich dữ liệu
//            try {
//                PatientDTO p = patientClient.getById(o.getPatientId());
//                if (p != null && !p.isDeleted()) {
//                    item.setPatientName(p.getFullName());
//                    item.setPatientGender(p.getGender());
//                    item.setPhone(p.getPhone());
//                    item.setPatientAge(ageFrom(p.getYob()));
//                }
//            } catch (Exception ignored) { /* Nếu Patient Service lỗi thì chỉ trả core fields */ }
//
//            return item;
//        });
//    }
//
//
//    //  DETAIL: Xem chi tiết 1 phiếu
//
//    @Transactional(readOnly = true)
//    public TestOrderDetail detail(Long id) {
//        // 1) Load order từ DB
//        TestOrder o = orderRepo.findById(id)
//                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
//                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));
//
//        // 2) Map sang DTO
//        TestOrderDetail dto = TestOrderDetail.builder()
//                .id(o.getId())
//                .status(o.getStatus())
//                .createdAt(o.getCreatedAt())
//                .patientId(o.getPatientId())
//                .createdByUserId(o.getCreatedByUserId())
//                .runByUserId(o.getRunByUserId())
//                .runAt(o.getRunAt())
//                .build();
//
//        // 3) Enrich Patient
//        try {
//            PatientDTO p = patientClient.getById(o.getPatientId());
//            if (p != null && !p.isDeleted()) {
//                dto.setPatientName(p.getFullName());
//                dto.setPatientGender(p.getGender());
//                dto.setPatientEmail(p.getEmail());
//                dto.setPatientAge(ageFrom(p.getYob()));
//            }
//        } catch (Exception ignored) {}
//
//        // 4) Thêm results nếu trạng thái là COMPLETED/REVIEWED/AI_REVIEWED
//        if (o.getStatus() == OrderStatus.COMPLETED
//                || o.getStatus() == OrderStatus.REVIEWED
//                || o.getStatus() == OrderStatus.AI_REVIEWED) {
//            dto.setResults(resultRepo.findByOrderId(o.getId()));
//        }
//
//        // 5) Luôn trả comments
//        dto.setComments(commentRepo.findByOrderIdOrderByCreatedAtAsc(o.getId()));
//
//        return dto;
//    }
//
//    //UPDATE: Cập nhật phiếu (status, runByUserId…)
//
//    @Transactional
//    public TestOrder update(Long id, TestOrderUpdateRequest req, Long operatorUserId) {
//        // 1) Tìm order chưa bị xoá
//        TestOrder o = orderRepo.findById(id)
//                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
//                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));
//
//        String before = safeJson(o);
//
//        // 2) Cập nhật status nếu có
//        if (req.getStatus() != null) {
//            // Bạn có thể bổ sung check transition hợp lệ tại đây (PENDING->COMPLETED,…)
//            o.setStatus(req.getStatus());
//        }
//
//        // 3) Cập nhật người chạy test nếu có
//        if (req.getRunByUserId() != null) {
//            o.setRunByUserId(req.getRunByUserId());
//            o.setRunAt(req.getRunAt() != null ? req.getRunAt() : LocalDateTime.now());
//        }
//
//        TestOrder saved = orderRepo.save(o);
//
//        // 4) Log audit với before/after
//        String after = safeJson(saved);
//        logAudit(saved.getId(), "UPDATE",
//                "{\"before\":" + before + ",\"after\":" + after + "}", operatorUserId);
//
//        return saved;
//    }
//
//    // DELETE (Soft delete)
//
//    @Transactional
//    public void softDelete(Long id, Long operatorUserId) {
//        // 1) Tìm order chưa bị xoá
//        TestOrder o = orderRepo.findById(id)
//                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
//                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại hoặc đã xoá"));
//
//        // 2) Đánh dấu deleted
//        o.setDeleted(true);
//        o.setDeletedByUserId(operatorUserId);
//        o.setDeletedAt(LocalDateTime.now());
//
//        orderRepo.save(o);
//
//        // 3) Ghi audit
//        logAudit(o.getId(), "DELETE", "{}", operatorUserId);
//    }
//
//
//    // Helper: ghi audit
//

    private void logAudit(Long orderId, String action, String detail, Long operatorUserId) {
        auditRepo.save(HistoryOrderAudit.builder()
                .orderId(orderId)
                .action(action)
                .detail(detail)
                .operatorUserId(operatorUserId)
                .build());
    }


    // 🛠️ Helper: convert object sang JSON an toàn

    private String safeJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            return "\"<json-error>\"";
        }
    }
}