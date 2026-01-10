package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.friendship.accept.FriendAcceptRequest;
import vn.hust.social.backend.dto.friendship.accept.FriendAcceptResponse;
import vn.hust.social.backend.dto.friendship.incoming.FriendIncomingResponse;
import vn.hust.social.backend.dto.friendship.outgoing.FriendOutgoingResponse;
import vn.hust.social.backend.dto.friendship.reject.FriendRejectRequest;
import vn.hust.social.backend.dto.friendship.request.FriendRequestRequest;
import vn.hust.social.backend.dto.friendship.request.FriendRequestResponse;
import vn.hust.social.backend.dto.friendship.unfriend.FriendUnfriendRequest;
import vn.hust.social.backend.dto.friendship.unfriend.FriendUnfriendResponse;
import vn.hust.social.backend.dto.friendship.check.CheckFriendshipResponse;
import vn.hust.social.backend.dto.friendship.get.GetFriendsResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.friendship.FriendshipService;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
@Tag(name = "Friendship", description = "Friendship management APIs")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final JwtUtils jwtUtils;

    @PostMapping("/request")
    @Operation(summary = "Send friend request", description = "Send a friend request to another user")
    public ApiResponse<FriendRequestResponse> friendRequest(
            @RequestBody FriendRequestRequest friendRequestRequest,
            HttpServletRequest request) {
        String requesterEmail = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendRequest(friendRequestRequest.receiverId(), requesterEmail));
    }

    @PostMapping("/accept")
    @Operation(summary = "Accept friend request", description = "Accept an incoming friend request")
    public ApiResponse<FriendAcceptResponse> friendAccept(@RequestBody FriendAcceptRequest friendAcceptRequest,
            HttpServletRequest request) {
        String receiverEmail = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse
                .success(friendshipService.friendAccept(friendAcceptRequest.friendRequestId(), receiverEmail));
    }

    @PostMapping("/reject")
    @Operation(summary = "Reject friend request", description = "Reject an incoming friend request")
    public ApiResponse<Void> friendReject(@RequestBody FriendRejectRequest friendRejectRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        friendshipService.friendReject(friendRejectRequest.friendRequestId(), email);
        return ApiResponse.success(null);
    }

    @PostMapping("/unfriend")
    @Operation(summary = "Unfriend user", description = "Unfriend an existing friend")
    public ApiResponse<FriendUnfriendResponse> friendUnfriend(@RequestBody FriendUnfriendRequest friendUnfriendRequest,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendUnfriend(friendUnfriendRequest.friendId(), email));
    }

    @GetMapping("/requests/incoming")
    @Operation(summary = "Get incoming requests", description = "Get list of incoming friend requests")
    public ApiResponse<FriendIncomingResponse> friendIncoming(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendIncoming(email));
    }

    @GetMapping("/requests/outgoing")
    @Operation(summary = "Get outgoing requests", description = "Get list of outgoing friend requests")
    public ApiResponse<FriendOutgoingResponse> friendOutgoing(HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendOutgoing(email));
    }

    @GetMapping("/list")
    @Operation(summary = "Get friends list", description = "Get list of friends with pagination")
    public ApiResponse<GetFriendsResponse> getFriends(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.getFriends(email, page, size));
    }

    @GetMapping("/check/{targetUserId}")
    @Operation(summary = "Check friendship status", description = "Check friendship status with another user")
    public ApiResponse<CheckFriendshipResponse> checkFriendship(
            @PathVariable String targetUserId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.checkFriendship(email, targetUserId));
    }
}
