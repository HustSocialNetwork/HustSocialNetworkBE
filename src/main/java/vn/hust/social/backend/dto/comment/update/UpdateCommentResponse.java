package vn.hust.social.backend.dto.comment.update;

import vn.hust.social.backend.dto.comment.CommentDTO;

public record UpdateCommentResponse(
        CommentDTO comment
) {
}
