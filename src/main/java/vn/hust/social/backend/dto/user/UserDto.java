package vn.hust.social.backend.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String displayName,
        LocalDateTime createdAt
) {}
