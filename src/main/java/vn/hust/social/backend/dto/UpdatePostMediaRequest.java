package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.PostMedia;

public record UpdatePostMediaRequest(
        String objectKey,
        PostMedia.Type type,
        int orderIndex,
        String operation
) {
}
