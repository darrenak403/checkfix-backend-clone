package com.datnguyen.testorderservices.service.imp;

import com.datnguyen.testorderservices.client.PatientClient;
import com.datnguyen.testorderservices.client.PatientDTO;
import com.datnguyen.testorderservices.client.UserClient;
import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.request.TestOrderUpdateRequest;
import com.datnguyen.testorderservices.dto.request.TestOrderUpdateStatusRequest;
import com.datnguyen.testorderservices.dto.response.*;
import com.datnguyen.testorderservices.entity.*;
import com.datnguyen.testorderservices.mapper.CommentMapper;
import com.datnguyen.testorderservices.mapper.TestOrderMapper;
import com.datnguyen.testorderservices.repository.*;
import com.datnguyen.testorderservices.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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
    private final JwtUtils jwtUtils;
    private final UserClient userClient;

    private static Integer ageFrom(LocalDate dob) {
        return (dob == null) ? null : Period.between(dob, LocalDate.now()).getYears();
    }

    //    @Transactional
//    public TestOrderCreationResponse create(TestOrderCreateRequest req) {
//        var patientResponse = getPatient(req.getPatientId());
//        RestResponse<UserResponse> user = userClient.getUser(req.getRunBy());
//        TestOrder order = TestOrder.builder()
//                .patientId(req.getPatientId())
//                .patientName(patientResponse.getFullName())
//                .email(patientResponse.getEmail())
//                .address(patientResponse.getAddress())
//                .phone(patientResponse.getPhone())
//                .yob(patientResponse.getYob())
//                .gender(patientResponse.getGender())
//                .status(OrderStatus.PENDING)
//                .createdBy(jwtUtils.getFullName())
//                .runBy(user.getData().getFullName())
//                .priority(req.getPriority())
//                .testType(req.getTestType())
//                .instrument(req.getInstrument())
//                .age(ageFrom(patientResponse.getYob()))
//                .deleted(false)
//                .build();
//
//        TestOrder saved = orderRepo.save(order);
//        logAudit(saved.getId(), "CREATE", safeJson(req), jwtUtils.getCurrentUserId());
//        return mapper.toTestOrderCreationResponse(saved);
//    }

    @Transactional
    public TestOrderCreationResponse create(TestOrderCreateRequest req) {
        var patientResponse = getPatient(req.getPatientId());
        RestResponse<UserResponse> user = userClient.getUser(req.getRunBy());

        String accessionNumber = generateAccessionNumber();
        TestOrder order = TestOrder.builder()
                .patientId(req.getPatientId())
                .patientName(patientResponse.getFullName())
                .email(patientResponse.getEmail())
                .address(patientResponse.getAddress())
                .phone(patientResponse.getPhone())
                .yob(patientResponse.getYob())
                .gender(patientResponse.getGender())
                .status(OrderStatus.PENDING)
                .createdBy(jwtUtils.getFullName())
                .runBy(user.getData().getFullName())
                .priority(req.getPriority())
                .testType(req.getTestType())
                .accessionNumber(accessionNumber)
                .instrument(req.getInstrument())
                .age(ageFrom(patientResponse.getYob()))
                .deleted(false)
                .build();

        TestOrder saved = orderRepo.save(order);
        logAudit(saved.getId(), "CREATE", safeJson(req), jwtUtils.getCurrentUserId());
        return mapper.toTestOrderCreationResponse(saved);
    }


    @Transactional(readOnly = true)
    public Page<TestOrderCreationResponse> list(OrderStatus status, Pageable pageable) {
        Page<TestOrder> page = (status == null)
                ? orderRepo.findByDeletedFalse(pageable)
                : orderRepo.findByDeletedFalseAndStatus(status, pageable);

        return page.map(mapper::toTestOrderCreationResponse);
    }

    @Transactional(readOnly = true)
    public TestOrderDetailResponse detail(Long id) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));
        List<Comment> comments = commentRepo.findByTestOrderIdAndStatus(o.getId(), CommentStatus.ACTIVE);
        o.setComments(comments);
        List<CommentResponse> commentResponses = comments.stream()
                .map(c -> {
                    long testOrderId = (c.getTestOrder() != null) ? c.getTestOrder().getId() : 0L;
                    long testResultId = (c.getTestResult() != null) ? c.getTestResult().getId() : 0L;

                    return CommentResponse.builder()
                            .commentId(c.getId())
                            .commentContent(c.getContent())
                            .testOrderId(testOrderId)
                            .doctorName(jwtUtils.getFullName())
                            .testResultId(testResultId)
                            .createdAt(c.getCreatedAt())
                            .build();
                })
                .toList();
        TestOrderDetailResponse resp = mapper.toTestOrderDetailResponse(o);
        resp.setComments(commentResponses);
//        var dto = TestOrderDetailResponse.builder()
//                .id(o.getId())
//                .status(o.getStatus())
//                .createdAt(o.getCreatedAt())
//                .patientId(o.getPatientId())
//                .runAt(o.getRunAt())
//                .comments(commentRepo.findByUserId(o.getId()))
//                .build();
//
//        try {
//            var p = getPatient(o.getPatientId());
//            dto.setPatientName(p.getFullName());
//            dto.setPatientGender(p.getGender());
//            dto.setPatientEmail(p.getEmail());
//            dto.setPatientAge(ageFrom(p.getYob()));
//        } catch (Exception ignored) {}

        return resp;
    }

    @Transactional
    public TestOrderCreationResponse updateStatus(Long id, TestOrderUpdateStatusRequest req) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));

        if (req.getStatus() != null) o.setStatus(req.getStatus());
        logAudit(o.getId(), "UPDATE", safeJson(o), jwtUtils.getCurrentUserId());
        orderRepo.save(o);
        return mapper.toTestOrderCreationResponse(o);
    }

    @Transactional
    public TestOrderCreationResponse update(Long id, TestOrderUpdateRequest req) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại"));
        RestResponse<UserResponse> user = userClient.getUser(req.getRunBy());
        if (StringUtils.hasText(req.getFullName())) o.setPatientName(req.getFullName());
        if (StringUtils.hasText(req.getPhone())) o.setPhone(req.getPhone());
        if (StringUtils.hasText(req.getAddress())) o.setAddress(req.getAddress());
        if (req.getYob() != null) o.setYob(req.getYob());
        if (StringUtils.hasText(req.getGender())) o.setGender(req.getGender());
        o.setAge(ageFrom(req.getYob()));
        if (req.getRunBy() != null) o.setRunBy(user.getData().getFullName());
        logAudit(o.getId(), "UPDATE", safeJson(o), jwtUtils.getCurrentUserId());
        orderRepo.save(o);
        return mapper.toTestOrderCreationResponse(o);
    }

    @Transactional
    public void softDelete(Long id) {
        TestOrder o = orderRepo.findById(id)
                .filter(ord -> !Boolean.TRUE.equals(ord.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Phiếu không tồn tại hoặc đã xoá"));

        o.setDeleted(true);
        orderRepo.save(o);

        logAudit(o.getId(), "DELETE", safeJson(o), jwtUtils.getCurrentUserId());
    }

    //    private PatientDTO getPatient(Long patientId) {
//        try {
//            RestResponse<PatientDTO> response = patientClient.getById(patientId);
//            if (response == null || response.getData().isDeleted())
//                throw new IllegalArgumentException("Bệnh nhân không tồn tại hoặc đã bị xoá");
//            return response.getData();
//        } catch (Exception e) {
//            log.error("Lỗi khi gọi Patient Service: {}", e.getMessage());
//            throw new IllegalArgumentException("Không kết nối được tới Patient Service");
//        }
//    }
    private PatientDTO getPatient(Long patientId) {
        try {
            RestResponse<PatientDTO> response = patientClient.getById(patientId);
            if (response == null || response.getData().isDeleted())
                throw new IllegalArgumentException("Bệnh nhân không tồn tại hoặc đã bị xoá");
            return response.getData();
        } catch (Exception e) {
            log.error("Lỗi khi gọi Patient Service: {}", e.getMessage());
            throw new IllegalArgumentException("Không kết nối được tới Patient Service");
        }
    }


    public PageResponse<TestOrderResponse> getAllOrdersByPatientId(Long patientId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<TestOrder> orders = orderRepo.findByPatientIdAndDeletedFalse(patientId, pageable);
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy phiếu xét nghiệm nào cho bệnh nhân này");
        }
        return PageResponse.<TestOrderResponse>builder()
                .currentPage(page)
                .totalPages(orders.getTotalPages())
                .pageSize(orders.getSize())
                .totalItems(orders.getTotalElements())
                .data(orders.getContent().stream().map(mapper::toTestOrderResponse).toList())
                .build();
    }


    private void logAudit(Long orderId, String action, String detail, Long operatorUserId) {
        auditRepo.save(HistoryOrderAudit.builder()
                .orderId(orderId)
                .action(action)
                .detail(detail)
                .operatorUserId(operatorUserId)
                .build());
    }
    private String generateAccessionNumber() {
        // Lấy số lượng phiếu hiện có để sinh mã kế tiếp
        long count = orderRepo.count() + 1;


        return String.format("ACC%03d", count);
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