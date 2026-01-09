package vn.hust.social.backend.dto.comment.create;

import jakarta.validation.constraints.NotBlank;
import vn.hust.social.backend.dto.media.CreateMediaRequest;

import java.util.List;

public record CreateCommentRequest(
                @NotBlank String postId,
                String content,
                String parentCommentId,
                List<CreateMediaRequest> createCommentMediaRequest) {
}
