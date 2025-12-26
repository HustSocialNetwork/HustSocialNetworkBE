package vn.hust.social.backend.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.like.Like;
import vn.hust.social.backend.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserIdAndTargetIdAndTargetType(UUID userId, UUID targetId, TargetType targetType);

    Optional<Like> findByUserIdAndTargetIdAndTargetType(UUID userId, UUID targetId, TargetType targetType);
}
