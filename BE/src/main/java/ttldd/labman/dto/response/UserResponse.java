package ttldd.labman.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String fullName;
    private Long id;
    private String role;
}
