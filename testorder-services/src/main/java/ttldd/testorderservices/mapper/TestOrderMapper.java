package ttldd.testorderservices.mapper;

import ttldd.testorderservices.dto.request.TestOrderCreateRequest;
import ttldd.testorderservices.dto.response.CommentResponse;
import ttldd.testorderservices.dto.response.TestOrderCreationResponse;
import ttldd.testorderservices.dto.response.TestOrderDetailResponse;
import ttldd.testorderservices.dto.response.TestOrderResponse;
import ttldd.testorderservices.entity.Comment;
import ttldd.testorderservices.entity.TestOrder;
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
