package vn.hust.social.backend.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.comment.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> getCommentsByPostPostId(UUID postPostId);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Comment c WHERE c.post.postId = :postId")
    List<Comment> findByPostId(@org.springframework.data.repository.query.Param("postId") UUID postId);

    Comment getCommentById(UUID id);

    Comment findCommentById(UUID id);

    @Override
    Optional<Comment> findById(UUID uuid);
}
