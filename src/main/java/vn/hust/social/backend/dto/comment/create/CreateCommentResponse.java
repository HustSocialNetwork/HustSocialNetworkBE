package vn.hust.social.backend.dto.comment.create;

import vn.hust.social.backend.entity.comment.Comment;

public record CreateCommentResponse(
        Comment comment
) {
}
