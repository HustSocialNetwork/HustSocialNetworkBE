package vn.hust.social.backend.dto.user.profile;

import vn.hust.social.backend.dto.user.ProfileDTO;

public record GetMeProfileResponse(
        ProfileDTO profileDTO
) {
}
