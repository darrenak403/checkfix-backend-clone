package ttldd.patientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientCode;

    private String fullName;

    private LocalDate yob;

    private String gender;

    private String address;

    private String avatarUrl;

    private String phone;

    private String email;


    private Long userId;

    private String createdBy;

    private LocalDateTime createdAt;

    private String modifiedBy;

    private boolean deleted = false;


}
