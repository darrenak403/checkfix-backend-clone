package jungle.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRequest {

//    @NotNull(message = "User ID is required")
    private Long userId;

    @Past(message = "Last test date must be in the past")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastTestDate;

    private String lastTestType;

    private String instrumentUsed;

}
