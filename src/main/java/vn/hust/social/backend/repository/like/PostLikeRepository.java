package vn.hust.social.backend.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.like.PostLike;

import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    boolean existsByUserIdAndPostPostId(UUID userId, UUID postPostId);

    void deletePostLikeByUserIdAndPostPostId(UUID userId, UUID postPostId);
}
