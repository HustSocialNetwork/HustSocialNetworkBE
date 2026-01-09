package vn.hust.social.backend.dto.friendship.get;

import vn.hust.social.backend.dto.FriendshipDTO;

import java.util.List;

public record GetFriendsResponse(
        List<FriendshipDTO> friendships) {
}
