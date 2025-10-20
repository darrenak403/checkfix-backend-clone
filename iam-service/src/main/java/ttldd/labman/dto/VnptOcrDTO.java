package ttldd.labman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VnptOcrDTO {

    @JsonProperty("id")
    private String id; // Số CCCD

    @JsonProperty("name")
    private String name; // Họ tên

    @JsonProperty("birth_day")
    private String birthDay; // Ngày sinh

    @JsonProperty("gender")
    private String gender; // Giới tính

    @JsonProperty("nationality")
    private String nationality; // Quốc tịch

    @JsonProperty("origin_location")
    private String originLocation; // Quê quán (dạng chuỗi)

    @JsonProperty("recent_location")
    private String recentLocation; // Nơi thường trú (dạng chuỗi)

    @JsonProperty("valid_date")
    private String validDate; // Ngày hết hạn

    // ----- Mặt sau -----
    @JsonProperty("issue_date")
    private String issueDate; // Ngày cấp

    @JsonProperty("issue_place")
    private String issuePlace; // Nơi cấp

    @JsonProperty("features")
    private String features; // Đặc điểm nhận dạng

    // ----- Dữ liệu cấu trúc (nếu cần) -----
    @JsonProperty("new_post_code")
    private List<PostCodeDTO> newPostCode;
}
