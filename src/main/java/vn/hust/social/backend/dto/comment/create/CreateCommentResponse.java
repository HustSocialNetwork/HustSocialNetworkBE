package vn.hust.social.backend.dto.comment.create;

import vn.hust.social.backend.dto.CommentDTO;

public record CreateCommentResponse(
        CommentDTO comment
) {
}
