package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import vn.hust.social.backend.dto.club.*;
import vn.hust.social.backend.security.JwtHeaderUtils;

import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.club.ClubService;

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

    @PutMapping("/{id}")
    @Operation(summary = "Update a club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<UpdateClubResponse> updateClub(
            @PathVariable UUID id,
            @RequestBody UpdateClubRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(clubService.updateClub(id, request, email));
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
    public ApiResponse<Void> inviteToFollow(
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

    @DeleteMapping("/{id}/moderators/{userId}")
    @Operation(summary = "Remove a moderator from the club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> removeModerator(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        clubService.removeModerator(id, userId, email);
        return ApiResponse.success(null);
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

    @GetMapping
    @Operation(summary = "List all clubs")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GetAllClubsResponse> getAllClubs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.getAllClubs(page, size, email));
    }

    @GetMapping("/search")
    @Operation(summary = "Search clubs by name (fulltext)")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<SearchClubsResponse> searchClubs(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.searchClubs(name, page, size, email));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a club by ID")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GetClubResponse> getClub(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.getClub(id, email));
    }

    @GetMapping("/{clubId}/moderators")
    @Operation(summary = "Get active club moderators")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GetActiveClubModeratorsResponse> getActiveClubModerators(
            @PathVariable UUID clubId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(clubService.getActiveClubModerators(clubId, email));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a club")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> deleteClub(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        clubService.deleteClub(id, email);
        return ApiResponse.success(null);
    }
}
