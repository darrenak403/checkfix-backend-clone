package ttldd.testorderservices.dto.response;

import ttldd.testorderservices.dto.TestParameterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResultResponse {
    private String accessionNumber;
    private String instrument;
    private String status;

    private List<TestParameterDTO> parameters;


}