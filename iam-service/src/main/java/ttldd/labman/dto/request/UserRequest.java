package ttldd.labman.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequest {
    @Email(message = "Invalid email format")
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,12}$",
            message = "Password must be 8-12 characters long and include uppercase, lowercase, and numbers"
    )
    private String password;
    private String sub;
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Full name must contain only letters")
    private String fullName;
}
