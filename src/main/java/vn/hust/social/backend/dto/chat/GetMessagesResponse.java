package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.MediaDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetMessagesResponse(
        UUID messageId,
        UUID senderId,
        String content,
        Instant createdAt,
        List<MediaDTO> medias
) {
}

