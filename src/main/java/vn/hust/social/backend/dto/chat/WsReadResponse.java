package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.UserDTO;

import java.time.Instant;
import java.util.UUID;

public record WsReadResponse(
        UUID conversationId,
        UUID messageId,
        UserDTO reader,
        Instant readAt) {
}
