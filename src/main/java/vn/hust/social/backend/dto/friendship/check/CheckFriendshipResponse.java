package vn.hust.social.backend.dto.friendship.check;

import vn.hust.social.backend.entity.enums.user.FriendshipStatus;

public record CheckFriendshipResponse(
        boolean isFriend,
        FriendshipStatus status) {
}
