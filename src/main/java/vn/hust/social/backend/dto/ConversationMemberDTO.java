package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.chat.MemberType;

import java.time.Instant;
import java.util.UUID;

public record ConversationMemberDTO(
        UUID id,
        Instant joinedAt,
        MemberType memberType,
        UserDTO member
) {
}
