package ttldd.testorderservices.mapper;

import ttldd.testorderservices.dto.response.CommentResponse;
import ttldd.testorderservices.entity.Comment;
import ttldd.testorderservices.entity.CommentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "commentContent", source = "content")
    @Mapping(target = "testOrderId", source = "testOrder.id")
    @Mapping(target = "testResultId", source = "testResult.id")
    @Mapping(target = "doctorName", source = "doctorName")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "replies", source = "replies", qualifiedByName = "mapReplies")
    CommentResponse toResponse(Comment comment);

    @Named("mapReplies")
    default List<CommentResponse> mapReplies(List<Comment> replies) {
        if (replies == null || replies.isEmpty()) {
            return List.of();
        }
        return replies.stream()
                .filter(reply -> reply.getStatus() == CommentStatus.ACTIVE)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    List<CommentResponse> toResponseList(List<Comment> comments);
}
