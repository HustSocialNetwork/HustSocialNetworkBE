package vn.hust.social.backend.dto.chat;

import java.util.UUID;

public record WsTypingRequest(
        UUID conversationId,
        boolean isTyping) {
}
