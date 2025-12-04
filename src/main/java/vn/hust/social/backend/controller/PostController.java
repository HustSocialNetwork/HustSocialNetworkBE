package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.post.create.CreatePostRequest;
import vn.hust.social.backend.dto.post.create.CreatePostResponse;
import vn.hust.social.backend.dto.post.get.GetPostResponse;
import vn.hust.social.backend.dto.post.update.UpdatePostRequest;
import vn.hust.social.backend.dto.post.update.UpdatePostResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.post.PostService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final JwtUtils jwtUtils;
    private final PostService postService;

    @GetMapping("/{postId}")
    public ApiResponse<GetPostResponse> getPost(@PathVariable String postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.getPost(postId, email));
    }

    @PostMapping
    public ApiResponse<CreatePostResponse> createPost(@RequestBody CreatePostRequest createPostRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.createPost(createPostRequest, email));
    }

    @PutMapping("/{postId}")
    public ApiResponse<UpdatePostResponse> updatePost(@PathVariable String postId, @RequestBody UpdatePostRequest updatePostRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(postService.updatePost(postId, updatePostRequest, email));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable String postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        postService.deletePost(postId, email);
        return ApiResponse.success(null);
    }
}
