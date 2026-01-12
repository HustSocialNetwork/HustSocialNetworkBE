package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.user.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String firstName,
        String lastName,
        String displayName,
        Instant createdAt,
        String avatarKey,
        String backgroundKey,
        String bio,
        Integer friendsCount,
        boolean emailVerified,
        UserRole role
) {}
