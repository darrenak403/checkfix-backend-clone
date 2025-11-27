package ttldd.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentRequest {

    @NotBlank(message = "Instrument name must not be blank")
    String name;

    @NotBlank(message = "Serial number must not be blank")
    String serialNumber;
}
