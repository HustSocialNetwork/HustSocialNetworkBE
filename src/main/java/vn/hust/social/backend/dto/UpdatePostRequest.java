package vn.hust.social.backend.dto;

import vn.hust.social.backend.entity.Post;

import java.util.List;

public record UpdatePostRequest(
        String content,
        Post.Visibility visibility,
        List<UpdatePostMediaRequest> updatePostMediaRequests
) {
}
