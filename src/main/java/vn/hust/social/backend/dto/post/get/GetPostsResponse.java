package vn.hust.social.backend.dto.post.get;

import vn.hust.social.backend.dto.PostDTO;

import java.util.List;

public record GetPostsResponse(
        List<PostDTO> posts
) {
}
