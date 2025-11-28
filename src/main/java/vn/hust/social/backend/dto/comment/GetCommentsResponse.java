package vn.hust.social.backend.dto.comment;

import java.util.List;

public record GetCommentsResponse(
        List<GetCommentResponse> comments
) {
}
