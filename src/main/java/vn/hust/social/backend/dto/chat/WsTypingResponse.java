package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.UserDTO;

import java.util.UUID;

public record WsTypingResponse(
        UUID conversationId,
        UserDTO user,
        boolean isTyping) {
}
