package vn.hust.social.backend.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.comment.CommentMedia;

public interface CommentMediaRepository extends JpaRepository<CommentMedia, String> {
}
