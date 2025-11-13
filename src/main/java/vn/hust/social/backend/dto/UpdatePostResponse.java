package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.Post;

import java.util.List;

public record UpdatePostResponse(
        Post post,
        List<UpdatePostMediaResponse> postMedias
) {
}
