package com.datnguyen.testorderservices.mapper;

import com.datnguyen.testorderservices.dto.request.TestOrderCreateRequest;
import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderCreationResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderDetailResponse;
import com.datnguyen.testorderservices.dto.response.TestOrderResponse;
import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.entity.TestOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestOrderMapper {
    TestOrder toEntity(TestOrderCreateRequest testOrder);
    TestOrderCreationResponse toTestOrderCreationResponse(TestOrder testOrder);
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "results", ignore = true)
    TestOrderDetailResponse toTestOrderDetailResponse(TestOrder testOrder);
    TestOrderResponse toTestOrderResponse(TestOrder testOrder);
    List<CommentResponse> toCommentResponseList(List<Comment> comments);
}
