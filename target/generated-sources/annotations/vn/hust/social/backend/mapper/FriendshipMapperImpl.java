package vn.hust.social.backend.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.dto.FriendshipDTO;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.enums.user.FriendshipStatus;
import vn.hust.social.backend.entity.friendship.Friendship;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-27T00:57:13+0700",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class FriendshipMapperImpl implements FriendshipMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public FriendshipDTO toDTO(Friendship friendship) {
        if ( friendship == null ) {
            return null;
        }

        UserDTO requester = null;
        UserDTO receiver = null;
        UUID id = null;
        FriendshipStatus status = null;

        requester = userMapper.toDTO( friendship.getRequester() );
        receiver = userMapper.toDTO( friendship.getReceiver() );
        id = friendship.getId();
        status = friendship.getStatus();

        FriendshipDTO friendshipDTO = new FriendshipDTO( id, requester, receiver, status );

        return friendshipDTO;
    }
}
