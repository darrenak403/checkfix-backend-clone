package jungle.patientservice.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserResponse {
    private String email;
    private String fullName;
    private Long id;
    private String role;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
}
