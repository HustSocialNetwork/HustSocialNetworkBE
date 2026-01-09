package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.user.FriendshipStatus;

import java.util.UUID;

public record FriendshipDTO(
        UUID id,
        UserDTO requester,
        UserDTO receiver,
        FriendshipStatus status
) {
}
