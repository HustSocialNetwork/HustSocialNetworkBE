package vn.hust.social.backend.mapper;

import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.dto.ProfileDTO;
import vn.hust.social.backend.dto.UserDTO;
import vn.hust.social.backend.entity.user.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-27T00:57:13+0700",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String firstName = null;
        String lastName = null;
        String displayName = null;
        Instant createdAt = null;
        String avatarKey = null;
        String backgroundKey = null;
        String bio = null;
        Integer friendsCount = null;
        boolean emailVerified = false;

        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        displayName = user.getDisplayName();
        createdAt = user.getCreatedAt();
        avatarKey = user.getAvatarKey();
        backgroundKey = user.getBackgroundKey();
        bio = user.getBio();
        friendsCount = user.getFriendsCount();
        emailVerified = user.isEmailVerified();

        UserDTO userDTO = new UserDTO( id, firstName, lastName, displayName, createdAt, avatarKey, backgroundKey, bio, friendsCount, emailVerified );

        return userDTO;
    }

    @Override
    public ProfileDTO toProfileDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String firstName = null;
        String lastName = null;
        String displayName = null;
        String avatarKey = null;
        String backgroundKey = null;
        String bio = null;
        Integer followerCount = null;
        Integer followingCount = null;
        Integer friendsCount = null;

        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        displayName = user.getDisplayName();
        avatarKey = user.getAvatarKey();
        backgroundKey = user.getBackgroundKey();
        bio = user.getBio();
        followerCount = user.getFollowerCount();
        followingCount = user.getFollowingCount();
        friendsCount = user.getFriendsCount();

        ProfileDTO profileDTO = new ProfileDTO( id, firstName, lastName, displayName, avatarKey, backgroundKey, bio, followerCount, followingCount, friendsCount );

        return profileDTO;
    }
}
