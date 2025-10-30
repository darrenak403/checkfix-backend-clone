package ttldd.testorderservices.dto;

import lombok.*;
import ttldd.testorderservices.entity.OrderStatus;
import ttldd.testorderservices.entity.PriorityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderDTO {

    private Long id;

    private Long patientId;

    private String patientName;

    private String email;

    private String address;

    private String phone;

    private String accessionNumber;

    private String gender;

    private LocalDate yob;

    private Integer age;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private PriorityStatus priority;

    private Long instrumentId;

    private String instrumentName;

    private LocalDateTime runAt;

    private String runBy;

    private String createdBy;

    private Boolean deleted;

}
