package ttldd.labman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PostCodeDTO {
    @JsonProperty("city")
    private List<Object> city;

    @JsonProperty("district")
    private List<Object> district;

    @JsonProperty("ward")
    private List<Object> ward;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("type")
    private String type;
}
