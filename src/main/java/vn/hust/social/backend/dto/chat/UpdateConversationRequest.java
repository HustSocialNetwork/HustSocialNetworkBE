package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.media.CreateMediaRequest;

public record UpdateConversationRequest(
        String name,
        CreateMediaRequest image) {
}
