package vn.hust.social.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record ClubWithStatusDTO(
        UUID id,
        String name,
        String description,
        String avatarKey,
        String backgroundKey,
        Integer followerCount,
        Instant createdAt,
        Instant updatedAt,
        boolean followedByUser) {

    public ClubWithStatusDTO(ClubDTO club, boolean followedByUser) {
        this(club.id(), club.name(), club.description(), club.avatarKey(), club.backgroundKey(),
                club.followerCount(), club.createdAt(), club.updatedAt(), followedByUser);
    }
}
