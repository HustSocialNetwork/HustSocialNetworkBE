package vn.hust.social.backend.dto.friendship.block;

import java.util.UUID;

public record FriendBlockRequest(
        UUID targetUserId
) {
}
