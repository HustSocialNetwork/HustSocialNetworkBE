package vn.hust.social.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.user.Friendship;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findByUser1IdAndUser2IdOrUser1IdAndUser2Id(
            UUID user1Id, UUID user2Id,
            UUID user2IdReverse, UUID user1IdReverse
    );
}
