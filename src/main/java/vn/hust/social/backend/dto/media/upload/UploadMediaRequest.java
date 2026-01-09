package vn.hust.social.backend.dto.media.upload;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record UploadMediaRequest(
        MediaType type,
        String name
) {}
