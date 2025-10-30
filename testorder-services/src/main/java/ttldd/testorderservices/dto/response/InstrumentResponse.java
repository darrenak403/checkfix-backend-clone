package ttldd.testorderservices.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ttldd.testorderservices.entity.InstrumentStatus;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentResponse {

    Long id;
    String name;
    String serialNumber;
    InstrumentStatus status;
    String createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime createdAt;
}
