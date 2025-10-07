package com.datnguyen.testorderservices.service;

import com.datnguyen.testorderservices.client.PatientClient;
import com.datnguyen.testorderservices.client.PatientDTO;
import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.request.TestOrderUpdateRequest;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderCreationResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderDetail;
import com.datnguyen.testorderservices.dto.response.TestOrderListItem;
import com.datnguyen.testorderservices.entity.HistoryOrderAudit;
import com.datnguyen.testorderservices.entity.OrderStatus;
import com.datnguyen.testorderservices.entity.TestOrder;
import com.datnguyen.testorderservices.mapper.TestOrderMapper;
import com.datnguyen.testorderservices.repository.CommentRepository;
import com.datnguyen.testorderservices.repository.HistoryOrderAuditRepository;
import com.datnguyen.testorderservices.repository.TestOrderRepository;
import com.datnguyen.testorderservices.repository.TestResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestOrderService {

    private final TestOrderRepository orderRepo;
    private final TestResultRepository resultRepo;
    private final CommentRepository commentRepo;
    private final HistoryOrderAuditRepository auditRepo;

    private final PatientClient patientClient;     // Feign Client sang Patient Service
    private final ObjectMapper om = new ObjectMapper().findAndRegisterModules();
 // convert Object -> JSON để log
    private final TestOrderMapper testOrderMapper;

    private static Integer ageFrom(LocalDate dob) {
        return (dob == null) ? null : Period.between(dob, LocalDate.now()).getYears();
    }


    // CREATE: Tạo phiếu xét nghiệm

    @Transactional
    public TestOrderCreationResponse create(TestOrderCreateRequest req) {
        // 1) Gọi Patient Service để check bệnh nhân tồn tại
        RestResponse<PatientDTO> p;
        try {
            p = patientClient.getById(req.getPatientId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Không kết nối được tới Patient Service");
        }
        if (p == null || p.getData().isDeleted()) {
            throw new IllegalArgumentException("Bệnh nhân không tồn tại hoặc đã bị xoá");
        }

        //Tạo TestOrder mới với trạng thái mặc định PENDING
        TestOrder order = TestOrder.builder()
                .patientId(req.getPatientId())
                .patientName(p.getData().getFullName())
                .email(p.getData().getEmail())
                .address(p.getData().getAddress())
                .phone(p.getData().getPhone())
                .yob(p.getData().getYob())
                .gender(p.getData().getGender())
                .status(OrderStatus.PENDING)
                .createdByUserId(req.getCreatedByUserId())
                .deleted(false)
                .build();

        TestOrder saved = orderRepo.save(order);

        TestOrderCreationResponse response = testOrderMapper.toTestOrderCreationResponse(order);
        log.info(p.getData().getFullName());
        // 3) Ghi lại Audit trail
        logAudit(saved.getId(), "CREATE", safeJson(req), req.getCreatedByUserId());

        return response;
    }


      //Xem danh sách phiếu (có filter status)

    @Transactional(readOnly = true)
    public Page<TestOrderCreationResponse> list(OrderStatus status, Pageable pageable) {
        // 1) Lấy danh sách phiếu từ DB
        Page<TestOrder> page = (status == null)
                ? orderRepo.findByDeletedFalse(pageable)
                : orderRepo.findByDeletedFalseAndStatus(status, pageable);

        // 2) Map sang DTO ListItem + enrich Patient info
        return page.map(o -> {
            TestOrderCreationResponse item = testOrderMapper.toTestOrderCreationResponse(o);

            // Gọi Patient Service enrich dữ liệu
            try {
                RestResponse<PatientDTO> p = patientClient.getById(o.getPatientId());
                if (p != null && !p.getData().isDeleted()) {
                    item.setPatientName(p.getData().getFullName());
                    item.setGender(p.getData().getGender());
                    item.setPhone(p.getData().getPhone());
                    item.setYob(p.getData().getYob());
                    item.setStatus(o.getStatus() == null ? OrderStatus.PENDING : o.getStatus());
                    item.setCreatedAt(o.getCreatedAt());
                }
            } catch (Exception ignored) {
                log.error(ignored.getMessage());
            }

            return item;
        });
    }


    //  DETAIL: Xem chi tiết 1 phiếu

    @Transactional(readOnly = true)
    public TestOrderDetail detail(Long id) {
        // 1) Load order từ DB
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));

        // 2) Map sang DTO
        TestOrderDetail dto = TestOrderDetail.builder()
                .id(o.getId())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .patientId(o.getPatientId())
                .createdByUserId(o.getCreatedByUserId())
                .runAt(o.getRunAt())
                .build();

        // 3) Enrich Patient
        try {
            RestResponse<PatientDTO> p = patientClient.getById(o.getPatientId());
            if (p != null && !p.getData().isDeleted()) {
                dto.setPatientName(p.getData().getFullName());
                dto.setPatientGender(p.getData().getGender());
                dto.setPatientEmail(p.getData().getEmail());
                dto.setPatientAge(ageFrom(p.getData().getYob()));
            }
        } catch (Exception ignored) {}

        // 4) Thêm results nếu trạng thái là COMPLETED/REVIEWED/AI_REVIEWED
        if (o.getStatus() == OrderStatus.COMPLETED
                || o.getStatus() == OrderStatus.REVIEWED
                || o.getStatus() == OrderStatus.AI_REVIEWED) {
            dto.setResults(resultRepo.findByOrderId(o.getId()));
        }

        // 5) Luôn trả comments
        dto.setComments(commentRepo.findByOrderIdOrderByCreatedAtAsc(o.getId()));

        return dto;
    }

    //UPDATE: Cập nhật phiếu (status, runByUserId…)

    @Transactional
    public TestOrderCreationResponse update(Long id, TestOrderUpdateRequest req) {
        // 1) Tìm order chưa bị xoá
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));


        if (StringUtils.hasText(req.getFullName())){
            o.setPatientName(req.getFullName());
        }
        if (StringUtils.hasText(req.getPhone())){
            o.setPhone(req.getPhone());
        }
        if (StringUtils.hasText(req.getAddress())){
            o.setAddress(req.getAddress());
        }
        if( req.getYob() != null){
            o.setYob(req.getYob());
        }


        orderRepo.save(o);

        return testOrderMapper.toTestOrderCreationResponse(o);
    }

    // DELETE (Soft delete)

    @Transactional
    public void softDelete(Long id, Long operatorUserId) {
        // 1) Tìm order chưa bị xoá
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại hoặc đã xoá"));

        // 2) Đánh dấu deleted
        o.setDeleted(true);
        o.setDeletedByUserId(operatorUserId);
        o.setDeletedAt(LocalDateTime.now());

        orderRepo.save(o);

        // 3) Ghi audit
        logAudit(o.getId(), "DELETE", "{}", operatorUserId);
    }


    // Helper: ghi audit

    private void logAudit(Long orderId, String action, String detail, Long operatorUserId) {
        auditRepo.save(HistoryOrderAudit.builder()
                .orderId(orderId)
                .action(action)
                .detail(detail)
                .operatorUserId(operatorUserId)
                .build());
    }

    // 🛠Helper: convert object sang JSON an toàn
    private String safeJson(Object obj) {
        try { return om.writeValueAsString(obj); }
        catch (Exception e) { return "\"<json-error>\""; }
    }
}