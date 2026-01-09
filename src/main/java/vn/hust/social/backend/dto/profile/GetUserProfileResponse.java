package vn.hust.social.backend.dto.profile;

import vn.hust.social.backend.dto.ProfileDTO;

public record GetUserProfileResponse(
        ProfileDTO profileDTO
) {
}
