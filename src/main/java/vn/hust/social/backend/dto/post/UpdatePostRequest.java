package vn.hust.social.backend.dto.post;

import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.post.Post;

import java.util.List;

public record UpdatePostRequest(
        String content,
        PostVisibility visibility,
        List<UpdatePostMediaRequest> updatePostMediaRequests
) {
}
