package vn.hust.social.backend.dto.post.delete;

import vn.hust.social.backend.dto.post.PostDTO;
import vn.hust.social.backend.entity.post.Post;
public record DeletePostResponse(
        PostDTO postDTO
) {
}
