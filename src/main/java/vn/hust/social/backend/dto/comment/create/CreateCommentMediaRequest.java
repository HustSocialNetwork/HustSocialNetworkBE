package vn.hust.social.backend.dto.comment.create;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record CreateCommentMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex
) {
}
