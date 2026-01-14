package vn.hust.social.backend.dto.club;

import vn.hust.social.backend.dto.ClubModeratorDTO;

import java.util.List;

public record GetActiveClubModeratorsResponse (
        List<ClubModeratorDTO> clubModerators
) {}
