package vn.hust.social.backend.dto;

import java.util.UUID;

public record ProfileDTO(
        UUID id,
        String firstName,
        String lastName,
        String displayName,
        String avatarKey,
        String backgroundKey,
        String bio,
        Integer followerCount,
        Integer followingCount
) {
}
