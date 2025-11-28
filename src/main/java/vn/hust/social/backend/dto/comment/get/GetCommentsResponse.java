package vn.hust.social.backend.dto.comment.get;

import java.util.List;

public record GetCommentsResponse(
        List<GetCommentResponse> comments
) {
}
