package ttldd.labman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VnptOcrDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("birth_day")
    private String birthDay;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("origin_location")
    private String originLocation;

    @JsonProperty("recent_location")
    private String recentLocation;

    @JsonProperty("valid_date")
    private String validDate;

    // ----- Back -----
    @JsonProperty("issue_date")
    private String issueDate;

    @JsonProperty("issue_place")
    private String issuePlace;

    @JsonProperty("features")
    private String features;

    // -----Data postcode -----
    @JsonProperty("new_post_code")
    private List<PostCodeDTO> newPostCode;
}
