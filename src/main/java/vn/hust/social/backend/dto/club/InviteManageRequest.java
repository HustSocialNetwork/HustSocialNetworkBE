package vn.hust.social.backend.dto.club;

import java.util.UUID;
import vn.hust.social.backend.entity.enums.club.ClubRole;

public record InviteManageRequest(
        UUID userId,
        ClubRole role) {
}
