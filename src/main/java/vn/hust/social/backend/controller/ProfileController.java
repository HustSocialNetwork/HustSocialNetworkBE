package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Profile", description = "User Profile APIs")
@PreAuthorize("hasRole('USER')")
public class ProfileController {
    private final JwtUtils jwtUtils;
    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Get profile of the current user")
    public ApiResponse<GetMeProfileResponse> getMeProfile(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.getMeProfile(email));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile", description = "Get profile of another user")
    public ApiResponse<GetUserProfileResponse> getUserProfile(@PathVariable UUID userId, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.getUserProfile(userId, email));
    }

    @PatchMapping("/update-profile")
    @Operation(summary = "Update profile", description = "Update current user's profile information")
    public ApiResponse<UpdateProfileResponse> updateUserProfile(@RequestBody UpdateProfileRequest updateProfileRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.updateUserProfile(updateProfileRequest, email));
    }

    @PostMapping("/search")
    @Operation(summary = "Search profiles", description = "Search for user profiles by keyword")
    public ApiResponse<SearchProfilesResponse> searchProfile(@RequestBody SearchProfilesRequest searchProfilesRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(profileService.searchProfiles(searchProfilesRequest.keyword(), email));
    }

}
