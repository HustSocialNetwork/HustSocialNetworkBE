package vn.hust.social.backend.dto.comment.create;

import java.util.List;

public record CreateCommentRequest(
        String postId,
        String content,
        List<CreateCommentMediaRequest> createCommentMediaRequest
) {
}
