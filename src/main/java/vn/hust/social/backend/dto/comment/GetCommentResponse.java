package vn.hust.social.backend.dto.comment;

import vn.hust.social.backend.dto.user.UserDto;

import java.util.List;
import java.util.UUID;

public record GetCommentResponse(
        UUID id,
        UserDto commenter,
        String content,
        Integer likesCount,
        List<GetCommentMediaResponse> commentMediaList
) {
}
