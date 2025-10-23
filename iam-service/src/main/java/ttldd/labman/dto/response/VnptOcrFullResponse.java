package ttldd.labman.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ttldd.labman.dto.VnptOcrDTO;

@Data
@Builder
public class VnptOcrFullResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("object")
    private VnptOcrDTO object;
}
