package ttldd.testorderservices.dto.request;


import ttldd.testorderservices.entity.PriorityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestOrderCreateRequest {
    @NotNull(message = "patientId is required")
    private Long patientId;
    // mức độ ưu tiên :
    private PriorityStatus priority;


    @NotNull(message = "instrumentId is required")
    private Long instrumentId;

    private Long runBy;

}