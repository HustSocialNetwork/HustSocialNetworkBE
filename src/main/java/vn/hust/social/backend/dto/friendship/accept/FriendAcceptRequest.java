package vn.hust.social.backend.dto.friendship.accept;

import java.util.UUID;

public record FriendAcceptRequest(
        UUID friendRequestId
) {
}
