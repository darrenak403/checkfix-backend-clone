package ttldd.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private InstrumentStatus status;

    @Column(nullable = false)
    private boolean isActive = true;

    private Instant deactivatedAt;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
