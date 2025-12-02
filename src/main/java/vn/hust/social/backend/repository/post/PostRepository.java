package vn.hust.social.backend.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<List<Post>> findByContentContainsIgnoreCase(String content);
    Optional<List<Post>> findByCreatedAt(LocalDateTime createdAt);
    Optional<Post> findByPostId(UUID postId);
    Page<Post> findPostsByUser(User user, Pageable pageable);
}
