package jungle.patientservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientResponse {
    private Long id;

    private Long userId;

    private String patientCode;

    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yob;

    private String gender;

    private String address;

    private String phone;

    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastTestDate;

    private String lastTestType;

    private String instrumentUsed;

    private Long createdBy;
}

