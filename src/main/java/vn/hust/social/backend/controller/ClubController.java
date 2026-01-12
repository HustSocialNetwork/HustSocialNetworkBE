package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import vn.hust.social.backend.security.JwtHeaderUtils;

import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.club.ClubService;
import vn.hust.social.backend.dto.club.ApplyManageResponse;
import vn.hust.social.backend.dto.club.ApplyRequest;
import vn.hust.social.backend.dto.club.ApproveApplicationResponse;
import vn.hust.social.backend.dto.club.CreateClubRequest;
import vn.hust.social.backend.dto.club.CreateClubResponse;
import vn.hust.social.backend.dto.club.InviteFollowResponse;
import vn.hust.social.backend.dto.club.InviteManageRequest;
import vn.hust.social.backend.dto.club.InviteManageResponse;
import vn.hust.social.backend.dto.club.InviteRequest;
import vn.hust.social.backend.dto.club.RejectApplicationResponse;
import vn.hust.social.backend.dto.club.GetFollowedClubsResponse;
import vn.hust.social.backend.dto.club.GetManagedClubsResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "Club", description = "Endpoints for managing clubs")
public class ClubController {

    private final ClubService clubService;
    private final JwtUtils jwtUtils;

    @PostMapping
    @Operation(summary = "Create a new club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<CreateClubResponse> createClub(
            @RequestBody CreateClubRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(clubService.createClub(request, email));
    }

    @PostMapping("/follow/{id}")
    @Operation(summary = "Follow a club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> followClub(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        clubService.followClub(id, email);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/follow/{id}")
    @Operation(summary = "Unfollow a club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> unfollowClub(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        clubService.unfollowClub(id, email);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/invite/follow")
    @Operation(summary = "Invite a student to follow the club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<InviteFollowResponse> inviteToFollow(
            @PathVariable UUID id,
            @RequestBody InviteRequest inviteRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.inviteToFollow(id, inviteRequest.userId(), email));
    }

    @PostMapping("/{id}/invite/manage")
    @Operation(summary = "Invite a student to manage the club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<InviteManageResponse> inviteToManage(
            @PathVariable UUID id,
            @RequestBody InviteManageRequest inviteManageRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(
                clubService.inviteToManage(id, inviteManageRequest.userId(), inviteManageRequest.role(), email));
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "Apply to be a moderator of the club (can't apply to be an admin)")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ApplyManageResponse> applyToManage(
            @PathVariable UUID id,
            @RequestBody ApplyRequest applyRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.applyToManage(id, applyRequest.message(), email));
    }

    @PutMapping("/{id}/applications/{userId}/approve")
    @Operation(summary = "Approve a moderator application")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ApproveApplicationResponse> approveApplication(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.approveApplication(id, userId, email));
    }

    @PutMapping("/{id}/applications/{userId}/reject")
    @Operation(summary = "Reject a moderator application")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<RejectApplicationResponse> rejectApplication(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.rejectApplication(id, userId, email));
    }

    @GetMapping("/me/following")
    @Operation(summary = "Get clubs followed by the current user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GetFollowedClubsResponse> getFollowedClubs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.getFollowedClubs(email, page, size));
    }

    @GetMapping("/me/managing")
    @Operation(summary = "Get clubs managed by the current user")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GetManagedClubsResponse> getManagedClubs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.getManagedClubs(email, page, size));
    }
}
