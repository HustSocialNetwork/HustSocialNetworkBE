package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.comment.create.CreateCommentRequest;
import vn.hust.social.backend.dto.comment.create.CreateCommentResponse;
import vn.hust.social.backend.dto.comment.get.GetCommentsResponse;
import vn.hust.social.backend.dto.comment.update.UpdateCommentRequest;
import vn.hust.social.backend.dto.comment.update.UpdateCommentResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.comment.CommentService;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentController {
    private final JwtUtils jwtUtils;
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public ApiResponse<GetCommentsResponse> getComments(@PathVariable("postId") String postId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(commentService.getComments(postId, email));
    }

    @PostMapping
    public ApiResponse<CreateCommentResponse> createComment(@RequestBody CreateCommentRequest createCommentRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(commentService.createComment(createCommentRequest, email));
    }

    @PutMapping("/{commentId}")
    public ApiResponse<UpdateCommentResponse> updateComment(@RequestBody UpdateCommentRequest updateCommentRequest,
            @PathVariable("commentId") String commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(commentService.updateComment(updateCommentRequest, commentId, email));
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable("commentId") String commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        commentService.deleteComment(commentId, email);
        return ApiResponse.success(null);
    }
}
