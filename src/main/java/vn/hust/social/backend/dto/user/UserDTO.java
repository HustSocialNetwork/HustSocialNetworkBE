package vn.hust.social.backend.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String firstName,
        String lastName,
        String displayName,
        LocalDateTime createdAt
) {}
