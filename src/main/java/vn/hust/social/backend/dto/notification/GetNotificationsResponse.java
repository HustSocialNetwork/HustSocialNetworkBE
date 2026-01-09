package vn.hust.social.backend.dto.notification;

import java.util.List;

public record GetNotificationsResponse(
        List<NotificationDTO> notifications,
        long total,
        int page,
        int size,
        int totalPages) {
}
