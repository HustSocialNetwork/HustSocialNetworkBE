package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.dto.comment.*;
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
    public ResponseEntity<GetCommentsResponse> getComments (@PathVariable("postId") String postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(commentService.getComments(postId, email));
    }

    @PostMapping
    public ResponseEntity<CreateCommentResponse> createComment (@RequestBody CreateCommentRequest createCommentRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(commentService.postComment(createCommentRequest, email));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComments (@PathVariable("commentId") String commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<UpdateCommentResponse> updateComment (@RequestBody UpdateCommentRequest updateCommentRequest, @PathVariable("commentId") String commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(commentService.updateComment(updateCommentRequest, commentId, email));
    }
}
