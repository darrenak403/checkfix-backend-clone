package ttldd.labman.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ttldd.labman.dto.UploadDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VnptUploadResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("object")
    private UploadDTO object;

}
