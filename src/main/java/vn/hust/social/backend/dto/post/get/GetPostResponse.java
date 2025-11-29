package vn.hust.social.backend.dto.post.get;

import vn.hust.social.backend.dto.post.PostDTO;
import vn.hust.social.backend.entity.post.Post;

import java.util.List;

public record GetPostResponse(
        PostDTO post
) {
}
