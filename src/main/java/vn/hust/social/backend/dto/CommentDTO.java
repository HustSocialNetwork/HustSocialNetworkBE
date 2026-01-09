package vn.hust.social.backend.dto;

import java.util.List;
import java.util.UUID;

public record CommentDTO(
                UUID id,
                UserDTO user,
                String content,
                int likesCount,
                List<MediaDTO> medias) {
}
