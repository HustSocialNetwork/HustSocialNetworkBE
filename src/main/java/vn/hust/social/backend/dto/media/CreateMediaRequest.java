package vn.hust.social.backend.dto.media;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record CreateMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex
) {
}
