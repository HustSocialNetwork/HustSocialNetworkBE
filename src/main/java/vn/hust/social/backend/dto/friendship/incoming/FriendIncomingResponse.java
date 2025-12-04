package vn.hust.social.backend.dto.friendship.incoming;

import vn.hust.social.backend.dto.FriendshipDTO;

import java.util.List;

public record FriendIncomingResponse(
        List<FriendshipDTO> incomingRequests
) {
}
