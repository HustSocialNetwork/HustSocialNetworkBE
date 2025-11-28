package vn.hust.social.backend.dto.media;

import vn.hust.social.backend.entity.enums.media.MediaType;

import java.util.UUID;

public record PostMediaDTO(
        UUID postMediaDTO,
        MediaType mediaType,
        String objectKey,
        int orderIndex
) {
}
