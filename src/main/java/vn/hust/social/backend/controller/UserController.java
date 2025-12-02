package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.dto.post.get.GetPostsResponse;
import vn.hust.social.backend.dto.user.profile.GetMeProfileResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.user.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<GetMeProfileResponse> getMeProfile(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(userService.getMeProfile(email));
    }

    @GetMapping("{userId}/posts")
    public ResponseEntity<GetPostsResponse> getPosts(@PathVariable String userId, @RequestParam int page, @RequestParam int pageSize, HttpServletRequest request) {
        String email =  JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(userService.getPosts(userId, page, pageSize, email));
    }
}
