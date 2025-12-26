package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.media.MediaTargetType;
import vn.hust.social.backend.entity.enums.media.MediaType;

import java.util.UUID;

public record MediaDTO(
        UUID mediaId,
        UUID targetId,
        MediaTargetType targetType,
        MediaType type,
        String objectKey,
        Integer orderIndex
) {
}
