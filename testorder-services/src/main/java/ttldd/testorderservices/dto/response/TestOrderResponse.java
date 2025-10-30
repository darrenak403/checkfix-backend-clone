package ttldd.testorderservices.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TestOrderResponse {
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

    private String priority;

    private Long instrumentId;

    private String instrumentName;

    private String createdBy;

    private String runBy;

    private String status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}
