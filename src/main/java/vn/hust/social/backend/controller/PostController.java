package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<GetPostResponse> getPost(@PathVariable String postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(postService.getPost(postId, email));
    }

    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(@RequestBody CreatePostRequest createPostRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(createPostRequest, email));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<UpdatePostResponse> updatePost(@PathVariable String postId, @RequestBody UpdatePostRequest updatePostRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(postService.updatePost(postId, updatePostRequest, email));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        postService.deletePost(postId, email);
        return ResponseEntity.noContent().build();
    }
}
