package vn.hust.social.backend.dto.comment.update;

import vn.hust.social.backend.entity.enums.media.MediaOperation;
import vn.hust.social.backend.entity.enums.media.MediaType;

public record UpdateCommentMediaRequest(
        MediaType type,
        String objectKey,
        int orderIndex,
        MediaOperation operation
) {
}
