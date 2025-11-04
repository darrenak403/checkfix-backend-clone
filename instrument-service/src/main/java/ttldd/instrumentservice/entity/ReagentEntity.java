package ttldd.instrumentservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
//hello
@Document(collection = "reagent_inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReagentEntity {
    @Id
    private String id;

    private ReagentType reagentType;
    private String reagentName;
    private String lotNumber;
    private int quantity;
    private String unit;
    private LocalDate expiryDate;

    private String vendorId;
    private String vendorName;
    private String vendorContact;

    private String installedBy;
    private LocalDate installDate;

    private ReagentStatus status; // INSTALLED, AVAILABLE, USED, EXPIRED
    private String remarks;
}
