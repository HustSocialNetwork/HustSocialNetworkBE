package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hust.social.backend.dto.FriendshipDTO;
import vn.hust.social.backend.entity.friendship.Friendship;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FriendshipMapper {
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "receiver", source = "receiver")
    FriendshipDTO toDTO(Friendship friendship);
}
