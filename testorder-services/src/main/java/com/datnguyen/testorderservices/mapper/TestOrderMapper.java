package com.datnguyen.testorderservices.mapper;

import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.response.TestOrderCreationResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderDetailResponse;
import com.datnguyen.testorderservices.entity.TestOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestOrderMapper {
    TestOrder toEntity(TestOrderCreateRequest testOrder);
    TestOrderCreationResponse toTestOrderCreationResponse(TestOrder testOrder);
    TestOrderDetailResponse toTestOrderDetailResponse(TestOrder testOrder);
}
