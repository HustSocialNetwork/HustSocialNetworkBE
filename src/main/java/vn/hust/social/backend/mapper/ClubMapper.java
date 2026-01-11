package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.hust.social.backend.dto.ClubDTO;
import vn.hust.social.backend.dto.ClubFollowerDTO;
import vn.hust.social.backend.dto.ClubModeratorDTO;
import vn.hust.social.backend.entity.club.Club;
import vn.hust.social.backend.entity.club.ClubFollower;
import vn.hust.social.backend.entity.club.ClubModerator;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ClubMapper {
    @Mapping(target = "user", source = "user")
    ClubModeratorDTO toClubModeratorDTO(ClubModerator clubModerator);

    @Mapping(target = "user", source = "user")
    ClubFollowerDTO toClubFollowerDTO(ClubFollower clubFollower);

    ClubDTO toClubDTO(Club club);
}
