package vn.hust.social.backend.dto.comment.get;

import vn.hust.social.backend.dto.CommentDTO;

import java.util.List;

public record GetCommentsResponse(
        List<CommentDTO> comments
) {
}
