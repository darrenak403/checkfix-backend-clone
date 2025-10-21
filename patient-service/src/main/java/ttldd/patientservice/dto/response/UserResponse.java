package ttldd.patientservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String email;
    private String fullName;
    private Long id;
    private String role;
    private String address;
    private String avatarUrl;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
}
