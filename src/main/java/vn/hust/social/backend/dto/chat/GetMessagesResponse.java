package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.MediaDTO;
import vn.hust.social.backend.entity.enums.chat.MessageType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetMessagesResponse(
        UUID messageId,
        UUID senderId,
        String senderName,
        String senderAvatar,
        String content,
        MessageType type,
        Instant createdAt,
        List<MediaDTO> medias) {
}
