package ttldd.labman.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ttldd.labman.dto.CardImgDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCardResponse {
    String identifyNumber;
    String fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    String birthDate;
    String gender;
    String recentLocation;
    String nationality;
    String issueDate;
    String validDate;
    String issuePlace;
    String features;
    List<CardImgDTO> cardImages;
}
