package vn.hust.social.backend.dto;

import java.util.UUID;

public record BlockDTO(
        UUID id,
        UserDTO blocker,
        UserDTO blocked
) {
}
