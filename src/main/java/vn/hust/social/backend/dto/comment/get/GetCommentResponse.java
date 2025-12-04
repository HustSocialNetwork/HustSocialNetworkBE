package vn.hust.social.backend.dto.comment.get;

import vn.hust.social.backend.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public record GetCommentResponse(
        UUID id,
        UserDTO commenter,
        String content,
        Integer likesCount,
        List<GetCommentMediaResponse> commentMediaList
) {
}
