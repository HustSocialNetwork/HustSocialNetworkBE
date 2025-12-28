package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.entity.enums.chat.ConversationType;

import java.util.List;
import java.util.UUID;

public record CreateConversationRequest(
                ConversationType type,
                String title,
                List<UUID> participantIds) {
}
