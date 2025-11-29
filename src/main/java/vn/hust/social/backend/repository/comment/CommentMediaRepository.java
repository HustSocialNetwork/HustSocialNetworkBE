package vn.hust.social.backend.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.comment.CommentMedia;

import java.util.List;

public interface CommentMediaRepository extends JpaRepository<CommentMedia, String> {
    CommentMedia getCommentMediaByObjectKey(String objectKey);
}
