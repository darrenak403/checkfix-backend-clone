package ttldd.testorderservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestParameterDTO {
    private Integer sequence;
    private String paramCode;
    private String paramName;
    private String value;
    private String unit;
    private String refRange;
    private String flag;
}