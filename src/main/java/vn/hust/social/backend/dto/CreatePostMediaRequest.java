package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.PostMedia;

public record CreatePostMediaRequest(
        String objectKey,
        PostMedia.Type type,
        int orderIndex
) {}