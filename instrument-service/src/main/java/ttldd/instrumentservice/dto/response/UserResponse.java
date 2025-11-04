package ttldd.instrumentservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
//hello
@Data
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
