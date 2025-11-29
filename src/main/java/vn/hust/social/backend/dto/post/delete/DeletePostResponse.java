package vn.hust.social.backend.dto.post.delete;

import vn.hust.social.backend.dto.post.PostDTO;
public record DeletePostResponse(
        PostDTO post
) {
}
