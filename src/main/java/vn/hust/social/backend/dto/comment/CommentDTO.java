package vn.hust.social.backend.dto.comment;

import vn.hust.social.backend.dto.media.CommentMediaDTO;
import vn.hust.social.backend.dto.user.UserDTO;

import java.util.List;
import java.util.UUID;

public record CommentDTO(
        UUID id,
        UserDTO user,
        String content,
        int likesCount,
        List<CommentMediaDTO> medias
) {
}
