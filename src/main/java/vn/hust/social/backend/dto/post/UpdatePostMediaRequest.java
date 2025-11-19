package vn.hust.social.backend.dto.post;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record UpdatePostMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex,
        String operation
) {
}
