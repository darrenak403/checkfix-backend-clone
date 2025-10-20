package ttldd.testorderservices.service;

import ttldd.testorderservices.dto.request.CommentDeleteRequest;
import ttldd.testorderservices.dto.request.CommentRequest;
import ttldd.testorderservices.dto.request.CommentUpdateRequest;
import ttldd.testorderservices.dto.response.CommentDeleteResponse;
import ttldd.testorderservices.dto.response.CommentResponse;
import ttldd.testorderservices.dto.response.CommentUpdateResponse;

public interface CommentService {
    CommentResponse addComment(CommentRequest commentRequest);
    CommentUpdateResponse updateComment(CommentUpdateRequest request);
    CommentDeleteResponse deleteComment(CommentDeleteRequest commentDeleteRequest);
}
