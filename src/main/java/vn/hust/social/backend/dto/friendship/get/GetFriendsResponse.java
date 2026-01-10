package vn.hust.social.backend.dto.friendship.get;

import vn.hust.social.backend.dto.UserDTO;

import java.util.List;

public record GetFriendsResponse(
                List<UserDTO> friends) {
}
