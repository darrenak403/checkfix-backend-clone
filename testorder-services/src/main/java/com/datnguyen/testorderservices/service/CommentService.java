package com.datnguyen.testorderservices.service;

import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.dto.request.CommentUpdateRequest;
import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.dto.response.CommentUpdateResponse;
import com.datnguyen.testorderservices.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(CommentRequest commentRequest);

    List<CommentResponse> getCommentByUserId(Long CommentRequest);
    CommentUpdateResponse updateComment(CommentUpdateRequest request);
}
