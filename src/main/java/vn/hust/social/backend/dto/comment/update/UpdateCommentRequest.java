package vn.hust.social.backend.dto.comment.update;

import java.util.List;

public record UpdateCommentRequest(
        String content,
        List<UpdateCommentMediaRequest> updateCommentMediaRequests
) {
}
