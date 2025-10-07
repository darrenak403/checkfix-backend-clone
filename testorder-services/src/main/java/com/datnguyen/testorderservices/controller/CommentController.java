package com.datnguyen.testorderservices.controller;

import com.datnguyen.testorderservices.dto.request.CommentRequest;
import com.datnguyen.testorderservices.dto.request.CommentUpdateRequest;
import com.datnguyen.testorderservices.dto.response.BaseResponse;
import com.datnguyen.testorderservices.dto.response.CommentResponse;
import com.datnguyen.testorderservices.dto.response.CommentUpdateResponse;
import com.datnguyen.testorderservices.entity.Comment;
import com.datnguyen.testorderservices.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest ) {
        Comment comment = commentService.addComment(commentRequest);
        if(comment != null){
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(200);
            baseResponse.setMessage("Comment added successfully");
            baseResponse.setData(null);
            return ResponseEntity.ok(baseResponse);
        } else {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(400);
            baseResponse.setMessage("Failed to add comment");
            baseResponse.setData(null);
            return ResponseEntity.badRequest().body(baseResponse);
        }

    }

    @GetMapping
    public ResponseEntity<?> getCommentByUserId(@RequestParam Long userId ) {
        List<CommentResponse> comments = commentService.getCommentByUserId(userId);
        if(comments != null){
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(200);
            baseResponse.setMessage("Comments fetched successfully");
            baseResponse.setData(comments);
            return ResponseEntity.ok(baseResponse);
        } else {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(400);
            baseResponse.setMessage("Failed to fetch comments");
            baseResponse.setData(null);
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/update")
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
}
