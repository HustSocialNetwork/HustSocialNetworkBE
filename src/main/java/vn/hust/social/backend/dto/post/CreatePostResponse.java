package vn.hust.social.backend.dto.post;

import vn.hust.social.backend.entity.post.Post;

import java.util.UUID;

public record CreatePostResponse(
        UUID postId
) {
}
