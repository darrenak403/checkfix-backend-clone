package com.datnguyen.testorderservices.service;

import com.datnguyen.testorderservices.dto.request.CommentDeleteRequest;
import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.dto.request.CommentUpdateRequest;
import com.datnguyen.testorderservices.dto.response.CommentDeleteResponse;
import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.dto.response.CommentUpdateResponse;

public interface CommentService {
    CommentResponse addComment(CommentRequest commentRequest);
    CommentUpdateResponse updateComment(CommentUpdateRequest request);
    CommentDeleteResponse deleteComment(CommentDeleteRequest commentDeleteRequest);
}
