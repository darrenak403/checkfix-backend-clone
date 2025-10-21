package ttldd.testorderservices.controller;

import ttldd.testorderservices.dto.request.CommentDeleteRequest;
import ttldd.testorderservices.dto.request.CommentRequest;
import ttldd.testorderservices.dto.request.CommentUpdateRequest;
import ttldd.testorderservices.dto.response.BaseResponse;
import ttldd.testorderservices.dto.response.CommentDeleteResponse;
import ttldd.testorderservices.dto.response.CommentResponse;
import ttldd.testorderservices.dto.response.CommentUpdateResponse;
import ttldd.testorderservices.service.CommentService;
import ttldd.testorderservices.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    @PreAuthorize("  hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest) {

        CommentResponse comment = commentService.addComment(commentRequest);
        if(comment != null){
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(200);
            baseResponse.setMessage("Comment added successfully");
            baseResponse.setData(comment);
            return ResponseEntity.ok(baseResponse);
        } else {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(400);
            baseResponse.setMessage("Failed to add comment");
            baseResponse.setData(null);
            return ResponseEntity.badRequest().body(baseResponse);
        }

    }

    @PutMapping("/update")
    @PreAuthorize("  hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<?> updateComment(@RequestBody CommentUpdateRequest commentRequest ) {

        try {
            CommentUpdateResponse comment = commentService.updateComment(commentRequest);
            if(comment != null){
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setStatus(200);
                baseResponse.setMessage("Comment updated successfully");
                baseResponse.setData(comment);
                return ResponseEntity.ok(baseResponse);
            } else {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setStatus(400);
                baseResponse.setMessage("Failed to update comment");
                baseResponse.setData(null);
                return ResponseEntity.badRequest().body(baseResponse);
            }

        } catch (RuntimeException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setStatus(400);
            errorResponse.setMessage("Error: " + e.getMessage());
            errorResponse.setData(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    @DeleteMapping
    @PreAuthorize("  hasAnyAuthority('ROLE_DOCTOR')")
    public ResponseEntity<?> deleteComment(@RequestBody CommentDeleteRequest commentRequest ) {
        BaseResponse baseResponse = new BaseResponse();
        CommentDeleteResponse comment = commentService.deleteComment(commentRequest);
        if (comment != null) {
            baseResponse.setStatus(200);
            baseResponse.setMessage("Comment deleted successfully");
            baseResponse.setData(comment);
            return ResponseEntity.ok(baseResponse);
        } else {
            baseResponse.setStatus(400);
            baseResponse.setMessage("Failed to delete comment");
            baseResponse.setData(null);
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }
}
