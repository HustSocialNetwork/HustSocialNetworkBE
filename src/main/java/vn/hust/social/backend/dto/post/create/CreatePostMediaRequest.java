package vn.hust.social.backend.dto.post.create;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record CreatePostMediaRequest(
        String objectKey,
        MediaType type,
        int orderIndex
) {}