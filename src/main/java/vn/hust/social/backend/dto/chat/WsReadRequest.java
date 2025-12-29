package vn.hust.social.backend.dto.chat;

import java.util.UUID;

public record WsReadRequest(
        UUID conversationId,
        UUID messageId) {
}
