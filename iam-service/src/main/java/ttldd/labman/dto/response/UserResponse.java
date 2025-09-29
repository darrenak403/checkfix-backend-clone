package ttldd.labman.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {
    private String email;
    private String fullName;
    private Long id;
    private String role;
    private String address;
    private Date dateOfBirth;
    private String gender;
    private String phone;
}
