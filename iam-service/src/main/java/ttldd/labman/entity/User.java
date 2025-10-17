package ttldd.labman.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "user")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String email;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone must be 10 digits starting with 0")
    private String phoneNumber;


    private String fullName;

    @Pattern(regexp = "^(\\d{9}|\\d{12})$", message = "Identify number must be 9 or 12 digits")
    private String identifyNumber;

    @Pattern(regexp = "^(male|female)$", message = "Gender must be male or female")
    private String gender;

    @Min(18)
    private Integer age;

    private String address;


    private LocalDate dateOfBirth;


    private String password;

    private String avatarUrl;

    private String googleId;
    private String loginProvider;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}

