package vn.hust.social.backend.dto.friendship.reject;

import java.util.UUID;

public record FriendRejectRequest(
        UUID friendRequestId
) {
}
