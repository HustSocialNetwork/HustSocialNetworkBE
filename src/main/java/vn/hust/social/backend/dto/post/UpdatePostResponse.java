package vn.hust.social.backend.dto.post;

import vn.hust.social.backend.entity.post.Post;

import java.util.List;

public record UpdatePostResponse(
        Post post,
        List<UpdatePostMediaResponse> postMedias
) {
}
