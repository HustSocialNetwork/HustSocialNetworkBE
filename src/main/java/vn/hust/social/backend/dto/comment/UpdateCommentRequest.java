package vn.hust.social.backend.dto.comment;

import vn.hust.social.backend.dto.post.UpdatePostMediaRequest;

import java.util.List;

public record UpdateCommentRequest(
        String content,
        List<UpdateCommentMediaRequest> updateCommentMediaRequests
) {
}
