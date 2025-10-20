package ttldd.labman.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCardRequest {
    String identifyNumber;
    String fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    String birthDate;
    String gender;
    String recentLocation;
    String nationality;
    String issueDate;
    String validDate;
    List<CardImageRequest> cardImages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardImageRequest {
        private String type;
        private String imageUrl;
    }

}
