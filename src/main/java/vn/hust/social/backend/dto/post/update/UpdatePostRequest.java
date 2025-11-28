package vn.hust.social.backend.dto.post.update;

import vn.hust.social.backend.entity.enums.post.PostVisibility;

import java.util.List;

public record UpdatePostRequest(
        String content,
        PostVisibility visibility,
        List<UpdatePostMediaRequest> updatePostMediaRequests
) {
}
