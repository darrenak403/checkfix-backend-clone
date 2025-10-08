package jungle.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentRequest {

    @NotBlank
    String name;

    @NotBlank
    String serialNumber;
}
