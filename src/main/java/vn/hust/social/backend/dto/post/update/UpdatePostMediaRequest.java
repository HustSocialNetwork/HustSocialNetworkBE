package vn.hust.social.backend.dto.post.update;

import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.media.MediaType;

public record UpdatePostMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex,
        MediaOperation operation
) {
}
