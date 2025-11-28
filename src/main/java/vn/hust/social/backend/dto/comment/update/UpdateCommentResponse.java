package vn.hust.social.backend.dto.comment.update;

import vn.hust.social.backend.entity.comment.Comment;

public record UpdateCommentResponse(
        Comment comment
) {
}
