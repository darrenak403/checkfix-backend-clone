package ttldd.labman.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDTO {

    @NotBlank
    private String fullName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfVisit;
}
