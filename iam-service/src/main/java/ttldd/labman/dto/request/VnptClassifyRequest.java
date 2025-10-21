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
public class VnptClassifyRequest {

    @JsonProperty("img_card")
    private String imgCard;

    @JsonProperty("client_session")
    private String clientSession;

    @JsonProperty("token")
    private String token;
}
