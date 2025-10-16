package com.datnguyen.testorderservices.mapper;

import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.entity.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    List<CommentResponse> toResponse(List<Comment> entity);
}
