package vn.hust.social.backend.dto.post;

import vn.hust.social.backend.dto.comment.CommentDTO;
import vn.hust.social.backend.dto.media.PostMediaDTO;
import vn.hust.social.backend.entity.enums.post.PostStatus;
import vn.hust.social.backend.entity.enums.post.PostVisibility;

import java.util.List;
import java.util.UUID;

public record PostDTO(
        UUID postId,
        UUID userId,
        String content,
        PostStatus status,
        PostVisibility visibility,
        int likesCount,
        int commentsCount,
        List<PostMediaDTO> medias,
        List<CommentDTO> comments
) {
}
