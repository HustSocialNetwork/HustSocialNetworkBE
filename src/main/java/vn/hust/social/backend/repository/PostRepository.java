package vn.hust.social.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.post.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<List<Post>> findByContentContainsIgnoreCase(String content);
    Optional<List<Post>> findByCreatedAt(LocalDateTime createdAt);
    Optional<Post> findByPostId(UUID postId);
}
