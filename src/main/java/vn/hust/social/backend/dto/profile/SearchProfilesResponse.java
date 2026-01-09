package vn.hust.social.backend.dto.profile;

import vn.hust.social.backend.dto.ProfileDTO;

import java.util.List;

public record SearchProfilesResponse(
        List<ProfileDTO> profileDTOs
) {
}
