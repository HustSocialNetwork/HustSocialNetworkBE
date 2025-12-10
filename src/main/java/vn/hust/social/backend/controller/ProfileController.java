package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.profile.*;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.user.ProfileService;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final JwtUtils jwtUtils;
    private final ProfileService profileService;

    @GetMapping("/me")
    public ApiResponse<GetMeProfileResponse> getMeProfile(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.getMeProfile(email));
    }

    @GetMapping("/{userId}")
    public ApiResponse<GetUserProfileResponse> getUserProfile(@PathVariable UUID userId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.getUserProfile(userId, email));
    }

    @PatchMapping("/update-profile")
    public ApiResponse<UpdateProfileResponse> updateUserProfile(@RequestBody UpdateProfileRequest updateProfileRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.updateUserProfile(updateProfileRequest, email));
    }

    @PostMapping("/search")
    public ApiResponse<SearchProfilesResponse> searchProfile(@RequestBody SearchProfilesRequest searchProfilesRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.searchProfiles(searchProfilesRequest.keyword(), email));
    }

}
