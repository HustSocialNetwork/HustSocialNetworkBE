package vn.hust.social.backend.dto.friendship.request;

import java.util.UUID;

public record FriendRequestRequest(
        UUID receiverId
) {
}
