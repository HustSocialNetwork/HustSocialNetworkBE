package vn.hust.social.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record ClubDTO(
        UUID id,
        String name,
        String description,
        String avatarKey,
        String backgroundKey,
        Integer followerCount,
        Instant createdAt,
        Instant updatedAt) {
}
