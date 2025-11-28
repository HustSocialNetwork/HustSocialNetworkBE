package vn.hust.social.backend.dto.post;

import java.util.UUID;

public record PostDTO(
        UUID postId,
        UUID userId,
        String content,
        String status,
        String visibility,

) {
}
