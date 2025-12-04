package vn.hust.social.backend.dto.comment.update;

import vn.hust.social.backend.dto.CommentDTO;

public record UpdateCommentResponse(
        CommentDTO comment
) {
}
