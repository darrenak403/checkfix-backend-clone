package ttldd.labman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadDTO {
    @JsonProperty("hash") // Giả sử tên trường chứa hash là "hash"
    private String hash;
    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("fileType")
    private String fileType;

    @JsonProperty("tokenId")
    private String tokenId;
}
