package vn.hust.social.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String firstName,
        String lastName,
        String displayName,
        LocalDateTime createdAt,
        String avatarKey,
        String backgroundKey,
        String bio,
        Integer friendsCount,
        boolean emailVerified
) {}
