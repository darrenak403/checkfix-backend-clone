package jungle.patientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private String phone;

    private String email;

    private LocalDate lastTestDate;

    private String lastTestType;

    private String instrumentUsed;

    private Long userId;

    private Long createdBy;

    private boolean deleted = false;


}
