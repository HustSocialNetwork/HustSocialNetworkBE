package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class LikeController {
    private final JwtUtils jwtUtils;
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ApiResponse<String> likePost (@PathVariable UUID postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.like(postId, TargetType.POST, email);
        return ApiResponse.success("Successfully liked post");
    }

    @DeleteMapping("/post/{postId}")
    public ApiResponse<String> unlikePost (@PathVariable UUID postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.unlike(postId, TargetType.POST, email);
        return ApiResponse.success("Successfully unliked post");
    }

    @PostMapping("/comment/{commentId}")
    public ApiResponse<String> likeComment (@PathVariable UUID commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.like(commentId, TargetType.COMMENT, email);
        return ApiResponse.success("Successfully liked comment");
    }

    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<String> unlikeComment(@PathVariable UUID commentId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        likeService.unlike(commentId, TargetType.COMMENT, email);
        return ApiResponse.success("Successfully unliked comment");
    }
}
