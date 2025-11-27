package ttldd.labman.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionRequest {
    @NotBlank(message = "Permission name must not be blank")
    private String name;
    private String description;
}
