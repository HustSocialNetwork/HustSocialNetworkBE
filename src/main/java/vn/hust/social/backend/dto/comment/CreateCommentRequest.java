package vn.hust.social.backend.dto.comment;

import java.util.List;

public record CreateCommentRequest(
        String postId,
        String content,
        List<CreateCommentMediaRequest> createCommentMediaRequest
) {
}
