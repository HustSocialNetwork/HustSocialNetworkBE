package vn.hust.social.backend.dto.friendship.unfriend;

import java.util.UUID;

public record FriendUnfriendRequest(
        UUID friendId
) {
}
