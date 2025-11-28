package vn.hust.social.backend.dto.post.create;

import vn.hust.social.backend.entity.enums.post.PostVisibility;

import java.util.List;

public record CreatePostRequest(
        String content,
        PostVisibility visibility,
        List<CreatePostMediaRequest> createPostMediaRequests
) {

}
