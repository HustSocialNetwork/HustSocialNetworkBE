package vn.hust.social.backend.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.comment.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> getCommentsByPostPostId(UUID postPostId);

    Comment getCommentById(UUID id);
}
