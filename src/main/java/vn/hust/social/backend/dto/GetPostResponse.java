package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.Post;

import java.util.List;

public record GetPostResponse(
        Post post,
        List<GetPostMediaResponse> postMedias
) {
}
