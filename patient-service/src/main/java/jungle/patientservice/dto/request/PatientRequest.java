package jungle.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank
    private String address;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ!!")
    private String phone;

    @NotBlank
    @Email(message = "Email không hợp lệ!!")
    private String email;

    @Past(message = "Last test date must be in the past")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastTestDate;

    private String lastTestType;

    private String instrumentUsed;

}
