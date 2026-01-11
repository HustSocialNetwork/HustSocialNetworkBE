package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.notification.GetNotificationsResponse;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.notification.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management APIs")
@PreAuthorize("hasRole('USER')")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @Operation(summary = "Get notifications", description = "Get list of notifications with pagination")
    public ApiResponse<GetNotificationsResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(notificationService.getNotifications(email, page, limit));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a specific notification as read")
    public ApiResponse<Void> markAsRead(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        notificationService.markAsRead(id, email);
        return ApiResponse.success(null);
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read")
    public ApiResponse<Void> markAllAsRead(
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        notificationService.markAllAsRead(email);
        return ApiResponse.success(null);
    }
}
