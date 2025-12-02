package vn.hust.social.backend.dto.media.upload;

import vn.hust.social.backend.entity.enums.media.MediaType;

public record UploadMediasRequest(
        MediaType type,
        String name
) {}
