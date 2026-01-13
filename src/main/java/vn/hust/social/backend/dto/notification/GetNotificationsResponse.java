package vn.hust.social.backend.dto.notification;

import java.util.List;

import vn.hust.social.backend.dto.NotificationDTO;

public record GetNotificationsResponse(
                List<NotificationDTO> notifications,
                long total,
                int page,
                int size,
                int totalPages) {
}
