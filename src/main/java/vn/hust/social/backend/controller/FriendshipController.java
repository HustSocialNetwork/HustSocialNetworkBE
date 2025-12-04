package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.friendship.FriendshipService;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final JwtUtils jwtUtils;

    @PostMapping("/request")
    public ApiResponse<FriendRequestResponse> friendRequest (@RequestBody FriendRequestRequest friendRequestRequest, HttpServletRequest request) {
        String requesterEmail = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendRequest(friendRequestRequest.receiverId(), requesterEmail));
    }

    @PostMapping("/accept")
    public ApiResponse<FriendAcceptResponse> friendAccept (@RequestBody FriendAcceptRequest friendAcceptRequest, HttpServletRequest request) {
        String receiverEmail = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendAccept(friendAcceptRequest.friendRequestId(), receiverEmail));
    }

    @PostMapping("/reject")
    public ApiResponse<Void> friendReject (@RequestBody FriendRejectRequest friendRejectRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        friendshipService.friendReject(friendRejectRequest.friendRequestId(), email);
        return ApiResponse.success(null);
    }

    @PostMapping("/unfriend")
    public ApiResponse<FriendUnfriendResponse> friendUnfriend (@RequestBody FriendUnfriendRequest friendUnfriendRequest, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendUnfriend(friendUnfriendRequest.friendId(), email));
    }

    @PostMapping("/requests/incoming")
    public ApiResponse<FriendIncomingResponse> friendIncoming (HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendIncoming(email));
    }

    @PostMapping("/requests/outgoing")
    public ApiResponse<FriendOutgoingResponse> friendOutgoing (HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(friendshipService.friendOutgoing(email));
    }
}
