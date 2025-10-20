package ttldd.testorderservices.mapper;

import ttldd.testorderservices.dto.response.CommentResponse;
import ttldd.testorderservices.entity.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    List<CommentResponse> toResponse(List<Comment> entity);
}
