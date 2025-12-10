package vn.hust.social.backend.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.like.CommentLike;

import java.util.UUID;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {
    boolean existsByUserIdAndCommentId(UUID userId, UUID commentId);

    void deleteCommentLikeByUserIdAndCommentId(UUID userId, UUID commentId);
}
