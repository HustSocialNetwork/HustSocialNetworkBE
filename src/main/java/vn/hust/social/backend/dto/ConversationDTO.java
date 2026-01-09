package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.chat.ConversationType;

import java.time.Instant;
import java.util.UUID;

public record ConversationDTO(
                UUID id,
                ConversationType type,
                String title,
                UserDTO createdBy,
                Instant createdAt,
                Instant updatedAt) {
}
