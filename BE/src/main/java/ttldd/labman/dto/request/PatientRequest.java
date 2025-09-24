package ttldd.labman.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;

    @NotBlank
    private String gender;

    @NotBlank
    private String address;

    @NotBlank
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ!!")
    private String phone;

    @NotBlank
    @Email(message = "Email không hợp lệ!!")
    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastTestDate;

    @NotBlank
    private String lastTestType;

    @NotBlank
    private String instrumentUsed;

}
