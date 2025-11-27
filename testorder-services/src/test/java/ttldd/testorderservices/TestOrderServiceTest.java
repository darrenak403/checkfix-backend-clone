package ttldd.testorderservices;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ttldd.event.dto.PatientUpdateEvent;
import ttldd.testorderservices.client.PatientClient;
import ttldd.testorderservices.client.PatientDTO;
import ttldd.testorderservices.client.UserClient;
import ttldd.testorderservices.client.WareHouseClient;
import ttldd.testorderservices.dto.request.TestOrderCreateRequest;
import ttldd.testorderservices.dto.response.*;
import ttldd.testorderservices.entity.*;
import ttldd.testorderservices.mapper.CommentMapper;
import ttldd.testorderservices.mapper.TestOrderMapper;
import ttldd.testorderservices.repository.CommentRepository;
import ttldd.testorderservices.repository.HistoryOrderAuditRepository;
import ttldd.testorderservices.repository.TestOrderRepository;
import ttldd.testorderservices.service.imp.TestOrderService;
import ttldd.testorderservices.util.JwtUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestOrderServiceTest {

    @Mock private TestOrderRepository orderRepo;
    @Mock private CommentRepository commentRepo;
    @Mock private HistoryOrderAuditRepository auditRepo;
    @Mock private PatientClient patientClient;
    @Mock private UserClient userClient;
    @Mock private WareHouseClient wareHouseClient;
    @Mock private TestOrderMapper mapper;
    @Mock private JwtUtils jwtUtils;

    @Mock private CommentMapper commentMapper;


    @Spy private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks private TestOrderService testOrderService;

    @Test
    void create_Success_GeneratesAccessionNumber() {
        // Given
        var request = TestOrderCreateRequest.builder()
                .patientId(1L)
                .runBy(100L)
                .instrumentId(123456L)
                .priority(PriorityStatus.MEDIUM)
                .build();

        when(patientClient.getById(1L)).thenReturn(RestResponse.<PatientDTO>builder()
                .data(PatientDTO.builder()
                        .fullName("Nguyễn Văn A")
                        .yob(LocalDate.of(1990, 1, 1))
                        .gender("MALE")
                        .phone("0901234567")
                        .email("a@example.com")
                        .address("Hà Nội")
                        .build())
                .build());

        when(userClient.getUser(100L)).thenReturn(RestResponse.<UserResponse>builder()
                .data(UserResponse.builder().fullName("Dr. B").build())
                .build());

        when(wareHouseClient.getById(123456L)).thenReturn(RestResponse.<InstrumentResponse>builder()
                .data(InstrumentResponse.builder().name("Hematology XN-1000").build())
                .build());

        when(orderRepo.count()).thenReturn(5L);
        when(jwtUtils.getFullName()).thenReturn("Admin");
        when(jwtUtils.getCurrentUserId()).thenReturn(99L);

        TestOrder savedOrder = TestOrder.builder().id(10L).accessionNumber("ACC006").build();
        when(orderRepo.save(any(TestOrder.class))).thenReturn(savedOrder);
        when(mapper.toTestOrderCreationResponse(savedOrder))
                .thenReturn(TestOrderCreationResponse.builder().id(10L).accessionNumber("ACC006").build());

        // When
        var response = testOrderService.create(request);

        // Then
        assertThat(response.getAccessionNumber()).isEqualTo("ACC006");
        verify(orderRepo).save(any(TestOrder.class));
        verify(auditRepo).save(argThat(a -> a.getAction().equals("CREATE")));
    }

    @Test
    void detail_Success_LoadParentComments() {
        Long orderId = 1L;
        TestOrder order = TestOrder.builder().id(orderId).patientName("Nguyễn Văn A").build();
        Comment comment = Comment.builder().id(5L).content("Kết quả bình thường").level(1).build();
        CommentResponse commentResponse = new CommentResponse(); // Create appropriate comment response object
        
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(commentRepo.findByTestOrderIdAndStatusAndLevelOrderByCreatedAtDesc(orderId, CommentStatus.ACTIVE, 1))
                .thenReturn(List.of(comment));
        when(mapper.toTestOrderDetailResponse(order))
                .thenReturn(TestOrderDetailResponse.builder().id(orderId).build());
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse); // Add this line

        var result = testOrderService.detail(orderId);

        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void softDelete_Success() {
        Long orderId = 1L;
        TestOrder order = TestOrder.builder().id(orderId).deleted(false).build();

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(jwtUtils.getCurrentUserId()).thenReturn(99L);

        testOrderService.softDelete(orderId);

        assertThat(order.getDeleted()).isTrue();
        verify(orderRepo).save(order);
        verify(auditRepo).save(argThat(a -> a.getAction().equals("DELETE")));
    }

    @Test
    void asyncTestOrderFromUser_UpdateAllOrdersOfPatient() {
        PatientUpdateEvent event = PatientUpdateEvent.builder()
                .id(1L)
                .fullName("Trần Thị C")
                .phone("0912345678")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .build();

        TestOrder order1 = TestOrder.builder().id(10L).patientId(1L).patientName("Old Name").build();
        TestOrder order2 = TestOrder.builder().id(11L).patientId(1L).build();

        when(orderRepo.findByPatientIdAndDeletedFalse(1L)).thenReturn(List.of(order1, order2));

        testOrderService.asyncTestOrderFromUser(event);

        assertThat(order1.getPatientName()).isEqualTo("Trần Thị C");
        assertThat(order1.getPhone()).isEqualTo("0912345678");
        assertThat(order1.getAge()).isEqualTo(30);
        verify(orderRepo, times(2)).save(any(TestOrder.class));
    }
}