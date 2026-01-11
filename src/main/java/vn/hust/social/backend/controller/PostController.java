package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.post.create.CreatePostRequest;
import vn.hust.social.backend.dto.post.create.CreatePostResponse;
import vn.hust.social.backend.dto.post.get.GetPostByPostIdResponse;
import vn.hust.social.backend.dto.post.get.GetPostsByUserIdResponse;
import vn.hust.social.backend.dto.post.get.GetPostsOfFollowingResponse;
import vn.hust.social.backend.dto.post.update.UpdatePostRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostResponse;
import vn.hust.social.backend.dto.post.delete.DeletePostResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.post.PostService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "Post management APIs")
@PreAuthorize("hasRole('USER')")
public class PostController {
    private final JwtUtils jwtUtils;
    private final PostService postService;

    @GetMapping("/{postId}")
    @Operation(summary = "Get post details", description = "Get detailed information of a post")
    public ApiResponse<GetPostByPostIdResponse> getPostByPostId(@PathVariable String postId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.getPostByPostId(postId, email));
    }

    @Validated
    @GetMapping()
    @Operation(summary = "Get user posts", description = "Get list of posts created by a specific user")
    public ApiResponse<GetPostsByUserIdResponse> getPostsByUserId(
            @RequestParam String userId,
            @RequestParam int page,
            @RequestParam @Max(50) int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.getPostsByUserId(userId, page, pageSize, email));
    }

    @Validated
    @GetMapping("/friends")
    @Operation(summary = "Get friends' posts", description = "Get feed of posts from friends")
    public ApiResponse<GetPostsOfFollowingResponse> getPostsOfFriends(
            @RequestParam int page,
            @RequestParam @Max(50) int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.getPostsOfFriends(page, pageSize, email));
    }

    @Validated
    @GetMapping("/all")
    @Operation(summary = "Get all posts", description = "Get list of all posts (admin/feed)")
    public ApiResponse<GetPostsByUserIdResponse> getAllPosts(
            @RequestParam int page,
            @RequestParam @Max(50) int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.getAllPosts(page, pageSize, email));
    }

    @PostMapping()
    @Operation(summary = "Create post", description = "Create a new post")
    public ApiResponse<CreatePostResponse> createPost(
            @RequestBody CreatePostRequest createPostRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.createPost(createPostRequest, email));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update post", description = "Update an existing post")
    public ApiResponse<UpdatePostResponse> updatePost(
            @PathVariable String postId,
            @RequestBody UpdatePostRequest updatePostRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.updatePost(postId, updatePostRequest, email));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Delete a post")
    public ApiResponse<DeletePostResponse> deletePost(
            @PathVariable String postId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        DeletePostResponse response = postService.deletePost(postId, email);
        return ApiResponse.success(response);
    }
}
