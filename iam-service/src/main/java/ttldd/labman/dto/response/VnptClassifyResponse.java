package ttldd.labman.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ttldd.labman.dto.VnptClassifyDTO;

@Data
public class VnptClassifyResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private int statusCode;

    // Trường "object" chứa thông tin type
    @JsonProperty("object")
    private VnptClassifyDTO object;
}
