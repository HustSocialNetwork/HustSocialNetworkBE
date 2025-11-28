package vn.hust.social.backend.dto.media;

import vn.hust.social.backend.entity.enums.media.MediaType;

import java.util.UUID;

public record CommentMediaDTO(
        UUID commentMediaId,
        MediaType type,
        String objectKey,
        int orderIndex
) {
}
