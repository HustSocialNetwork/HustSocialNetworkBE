package vn.hust.social.backend.dto.post.update;

import vn.hust.social.backend.dto.post.PostDTO;
import vn.hust.social.backend.entity.post.Post;

import java.util.List;

public record UpdatePostResponse(
        PostDTO postDTO,
        List<UpdatePostMediaResponse> postMedias
) {
}
