package vn.hust.social.backend.service.target;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.entity.comment.Comment;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.comment.CommentRepository;
import vn.hust.social.backend.service.comment.CommentService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentTargetStrategy implements TargetStrategy {
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @Override
    public TargetType getTargetType() {
        return TargetType.COMMENT;
    }

    @Override
    public void validateView(User user, UUID targetId) {
        Comment comment = commentRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));
        if (!commentService.canViewComment(user.getId(), comment)) {
            throw new ApiException(ResponseCode.CANNOT_VIEW_COMMENT);
        }
    }

    @Override
    public void increaseLike(UUID targetId) {
        Comment comment = commentRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));
        comment.setLikesCount(comment.getLikesCount() + 1);
        commentRepository.save(comment);
    }

    @Override
    public void decreaseLike(UUID targetId) {
        Comment comment = commentRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));
        comment.setLikesCount(comment.getLikesCount() - 1);
        commentRepository.save(comment);
    }

    @Override
    public User getOwner(UUID targetId) {
        Comment comment = commentRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(ResponseCode.COMMENT_NOT_FOUND));
        return comment.getUser();
    }
}
