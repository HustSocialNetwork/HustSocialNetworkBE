package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.media.CreateMediaRequest;

import java.util.List;

public record SendMessageRequest(
        String content,
        List<CreateMediaRequest> medias
) {
}
