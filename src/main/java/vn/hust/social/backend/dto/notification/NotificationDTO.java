package vn.hust.social.backend.dto.notification;

import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.enums.notification.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationDTO(
        UUID id,
        UserDTO actor,
        UserDTO recipient,
        NotificationType targetType,
        UUID targetId,
        String message,
        boolean isRead,
        Instant createdAt,
        Instant updatedAt) {
}
