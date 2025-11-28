package vn.hust.social.backend.dto.comment;

import vn.hust.social.backend.dto.media.CommentMediaDTO;

import java.util.List;
import java.util.UUID;

public record CommentDTO(
        UUID commentId,
        String content,
        int likesCount,
        List<CommentMediaDTO> medias
) {
}
