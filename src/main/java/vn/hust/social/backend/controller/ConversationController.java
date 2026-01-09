package vn.hust.social.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.chat.GetMessagesResponse;
import vn.hust.social.backend.dto.chat.SendMessageRequest;
import vn.hust.social.backend.dto.chat.SendMessageWithMediaResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.chat.ConversationService;
import vn.hust.social.backend.dto.chat.CreateConversationRequest;
import vn.hust.social.backend.dto.chat.CreateConversationResponse;
import vn.hust.social.backend.dto.chat.GetConversationResponse;
import vn.hust.social.backend.dto.chat.AddMemberRequest;
import vn.hust.social.backend.dto.chat.UpdateConversationRequest;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import vn.hust.social.backend.dto.chat.WsReadRequest;
import vn.hust.social.backend.dto.chat.WsTypingRequest;
import java.security.Principal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final JwtUtils jwtUtils;
    private final ConversationService conversationService;

    @MessageMapping("/chat.typing")
    public void broadcastTyping(
            @Payload WsTypingRequest request,
            Principal principal) {
        conversationService.broadcastTyping(request, principal);
    }

    @MessageMapping("/chat.read")
    public void markMessageAsRead(
            @Payload WsReadRequest request,
            Principal principal) {
        conversationService.markMessageAsRead(request, principal);
    }

    @PostMapping
    public ApiResponse<CreateConversationResponse> createConversation(
            @RequestBody CreateConversationRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(conversationService.createConversation(request, email));
    }

    @GetMapping
    public ApiResponse<List<GetConversationResponse>> getConversationsOfUser(
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(conversationService.getConversationsOfUser(email));
    }

    @GetMapping("/{conversationId}/messages")
    public ApiResponse<List<GetMessagesResponse>> getMessages(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(conversationService.getMessages(conversationId, limit, after, before, email));
    }

    @PostMapping("/{conversationId}/messages")
    public ApiResponse<SendMessageWithMediaResponse> sendMessage(
            @PathVariable UUID conversationId,
            @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(conversationService.sendMessage(conversationId, request, email));
    }

    @PostMapping("/{conversationId}/members")
    public ApiResponse<GetConversationResponse> addMembers(
            @PathVariable UUID conversationId,
            @RequestBody AddMemberRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(conversationService.addMembers(conversationId, request, email));
    }

    @DeleteMapping("/{conversationId}/members/{memberId}")
    public ApiResponse<GetConversationResponse> removeMember(
            @PathVariable UUID conversationId,
            @PathVariable UUID memberId,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(conversationService.removeMember(conversationId, memberId, email));
    }

    @PutMapping("/{conversationId}")
    public ApiResponse<GetConversationResponse> updateConversation(
            @PathVariable UUID conversationId,
            @RequestBody UpdateConversationRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(conversationService.updateConversation(conversationId, request, email));
    }

    @PutMapping("/{conversationId}/members/{memberId}/promote")
    public ApiResponse<Void> promoteMemberToAdmin(
            @PathVariable UUID conversationId,
            @PathVariable UUID memberId,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        conversationService.promoteMemberToAdmin(conversationId, memberId, email);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{conversationId}")
    public ApiResponse<Void> deleteConversation(
            @PathVariable UUID conversationId,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        conversationService.deleteConversation(conversationId, email);
        return ApiResponse.success(null);
    }
}
