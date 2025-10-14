package jungle.warehouseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
}
