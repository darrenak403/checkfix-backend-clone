package ttldd.testorderservices.dto.response;

import ttldd.testorderservices.entity.OrderStatus;

import ttldd.testorderservices.entity.PriorityStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderCreationResponse {

    private Long id;

    private Long patientId;
    private String accessionNumber;

    private String patientName;

    private String email;

    private String address;

    private String phone;

    private String gender;

    private LocalDate yob;
    private Integer  age;

    @Enumerated(EnumType.STRING)
    private PriorityStatus priority;

    private Long instrumentId;

    private String instrumentName;

    private String createdBy;

    private String runBy;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
