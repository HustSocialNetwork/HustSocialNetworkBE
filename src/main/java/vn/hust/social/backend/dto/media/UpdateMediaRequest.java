package vn.hust.social.backend.dto.media;

import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.media.MediaType;

public record UpdateMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex,
        MediaOperation operation) {
}
