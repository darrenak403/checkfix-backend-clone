package ttldd.instrumentservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private String status;

    private LocalDateTime createdAt;

    private String priority;

    private Long instrumentId;

    private String instrumentName;

    private LocalDateTime runAt;

    private String runBy;

    private String createdBy;

    private Boolean deleted;

}
