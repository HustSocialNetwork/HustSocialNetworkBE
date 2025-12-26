package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.enums.post.PostStatus;
import vn.hust.social.backend.entity.enums.post.PostVisibility;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostDTO(
        UUID postId,
        UserDTO user,
        String content,
        PostStatus status,
        PostVisibility visibility,
        int likesCount,
        int commentsCount,
        List<MediaDTO> medias,
        Instant createdAt
) {
}
