package jungle.warehouseservice.dto.response;

import jakarta.persistence.*;
import jungle.warehouseservice.entity.InstrumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class InstrumentUpdateResponse {

    private Long id;


    private String name;


    private String serialNumber;


    private InstrumentStatus status;


    private boolean isActive = true;

    private Instant deactivatedAt;

    private String createdBy;

    private LocalDateTime createdAt;

}
