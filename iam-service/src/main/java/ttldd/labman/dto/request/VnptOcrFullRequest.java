package ttldd.labman.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VnptOcrFullRequest {
    @JsonProperty("img_front")
    private String imgFront;

    @JsonProperty("img_back")
    private String imgBack;

    @JsonProperty("client_session")
    private String clientSession;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("crop_param")
    private String cropParam;

    @JsonProperty("validate_postcode")
    private Boolean validatePostcode;

    @JsonProperty("token")
    private String token;
}
