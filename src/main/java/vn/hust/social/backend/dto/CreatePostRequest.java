package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.Post;

import java.util.List;

public record CreatePostRequest(
        String content,
        Post.Visibility visibility,
        List<CreatePostMediaRequest> createPostMediaRequests
) {

}
