package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.club.ClubFollowerStatus;

import java.time.Instant;
import java.util.UUID;

public record ClubFollowerDTO(
                UUID id,
                UserDTO user,
                ClubFollowerStatus status,
                Instant createdAt,
                Instant updatedAt) {
}
