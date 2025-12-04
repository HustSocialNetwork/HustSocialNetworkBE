package vn.hust.social.backend.dto.friendship.outgoing;

import vn.hust.social.backend.dto.FriendshipDTO;

import java.util.List;

public record FriendOutgoingResponse(
        List<FriendshipDTO> outgoingRequests
) {
}
