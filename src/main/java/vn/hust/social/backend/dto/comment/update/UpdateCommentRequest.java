package vn.hust.social.backend.dto.comment.update;

import vn.hust.social.backend.dto.media.UpdateMediaRequest;

import java.util.List;

public record UpdateCommentRequest(
                String content,
                List<UpdateMediaRequest> updateCommentMediaRequests) {
}
