package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.club.ClubModeratorStatus;
import vn.hust.social.backend.entity.enums.club.ClubRole;

import java.time.Instant;
import java.util.UUID;

public record ClubModeratorDTO(
                UUID id,
                UserDTO user,
                ClubRole role,
                ClubModeratorStatus status,
                Instant createdAt,
                Instant updatedAt) {
}
