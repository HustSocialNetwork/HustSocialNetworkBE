package vn.hust.social.backend.dto.chat;

import java.util.List;
import java.util.UUID;

import vn.hust.social.backend.dto.media.CreateMediaRequest;

public record WsSendMessageRequest(
        UUID conversationId,
        String content,
        List<CreateMediaRequest> medias) {
}
