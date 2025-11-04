package ttldd.testorderservices.dto.response;

import ttldd.testorderservices.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderDetailResponse {
    private Long id;
    private Long patientId;
    private String accessionNumber;
    private String patientName;
    private String gender;
    private String email;
    private String phone;
    private Integer age;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yob;

    private String address;
    private String priority;


    private Long instrumentId;

    private String instrumentName;

    private String createdBy;
    private String runBy;
    private OrderStatus status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime runAt;
    private List<TestResultResponse> results;
    private List<CommentResponse> comments;

}