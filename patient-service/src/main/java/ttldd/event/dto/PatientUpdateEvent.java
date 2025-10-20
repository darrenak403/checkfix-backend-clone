package ttldd.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateEvent {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private Long userId;
}
