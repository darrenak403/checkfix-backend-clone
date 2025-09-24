package ttldd.labman.dto.response;

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

    private String name;

    private LocalDate yob;

    private String gender;

    private String address;

    private String phone;

    private String email;

    private LocalDate lastTestDate;

    private String lastTestType;

    private String instrumentUsed;
}

