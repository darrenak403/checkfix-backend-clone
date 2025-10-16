package com.datnguyen.testorderservices.mapper;

import com.datnguyen.testorderservices.dto.TestParameterDTO;
import com.datnguyen.testorderservices.dto.response.TestResultResponse;
import com.datnguyen.testorderservices.entity.TestResult;
import com.datnguyen.testorderservices.entity.TestResultParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestResultMapper {

    // 🔹 Chuyển từ TestResult → DTO
    @Mapping(source = "accessionNumber", target = "accessionNumber")
    @Mapping(source = "instrumentName", target = "instrument")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "parameters", target = "parameters")
    TestResultResponse toDto(TestResult entity);

    // 🔹 Chuyển từ Parameter → DTO
    List<TestParameterDTO> toParamDtos(List<TestResultParameter> parameters);
}