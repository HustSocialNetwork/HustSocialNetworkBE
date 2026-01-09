package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.chat.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageDTO(
                UUID id,
                String content,
                MessageType type,
                UserDTO sender,
                ConversationDTO conversation,
                Instant createdAt,
                Instant updatedAt) {
}
