package vn.hust.social.backend.dto.comment;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record CreateCommentMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex
) {
}
