package vn.hust.social.backend.dto.club;

import vn.hust.social.backend.dto.ClubDTO;
import java.util.List;

public record GetFollowedClubsResponse(
        List<ClubDTO> clubs) {
}
