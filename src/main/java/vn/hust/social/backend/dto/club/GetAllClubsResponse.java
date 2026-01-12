package vn.hust.social.backend.dto.club;

import vn.hust.social.backend.dto.ClubWithStatusDTO;
import java.util.List;

public record GetAllClubsResponse(
        List<ClubWithStatusDTO> clubs) {
}
