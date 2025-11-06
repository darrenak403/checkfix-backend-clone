package ttldd.testorderservices.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_id", nullable = false)
    @JsonIgnore
    private TestOrder testOrder;

    private Long patientId;
    private String accessionNumber;
    private String instrumentName;

    @Column(length = 30)
    private String status; // COMPLETE / AI_REVIEW / REVIEWED / REJECTED
//    private Long commentId; // optional

    @Lob
    private String parseHl7; // raw HL7

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestResultParameter> parameters;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }



    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL)
    private List<Comment> comments;
}
