package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.like.LikeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "Like/Unlike APIs")
@PreAuthorize("hasRole('USER')")
public class LikeController {
    private final JwtUtils jwtUtils;
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    @Operation(summary = "Like post", description = "Like a post")
    public ApiResponse<String> likePost(@PathVariable UUID postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.like(postId, TargetType.POST, email);
        return ApiResponse.success("Successfully liked post");
    }

    @DeleteMapping("/post/{postId}")
    @Operation(summary = "Unlike post", description = "Unlike a post")
    public ApiResponse<String> unlikePost(@PathVariable UUID postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.unlike(postId, TargetType.POST, email);
        return ApiResponse.success("Successfully unliked post");
    }

    @PostMapping("/comment/{commentId}")
    @Operation(summary = "Like comment", description = "Like a comment")
    public ApiResponse<String> likeComment(@PathVariable UUID commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.like(commentId, TargetType.COMMENT, email);
        return ApiResponse.success("Successfully liked comment");
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "Unlike comment", description = "Unlike a comment")
    public ApiResponse<String> unlikeComment(@PathVariable UUID commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.unlike(commentId, TargetType.COMMENT, email);
        return ApiResponse.success("Successfully unliked comment");
    }
}
