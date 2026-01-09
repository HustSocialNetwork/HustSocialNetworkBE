package vn.hust.social.backend.repository.friendship;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.enums.user.FriendshipStatus;
import vn.hust.social.backend.entity.friendship.Friendship;
import vn.hust.social.backend.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findFriendshipById(UUID id);

    List<Friendship> findFriendshipsByReceiverAndStatus(User receiver, FriendshipStatus status);

    List<Friendship> findFriendshipsByRequesterAndStatus(User requester, FriendshipStatus status);

    Optional<Friendship> findFriendshipsByReceiverIdAndReceiverIdOrRequesterIdAndReceiverId(UUID receiverId,
            UUID receiverId1, UUID requesterId, UUID receiverId11);

    @Query("SELECT f FROM Friendship f WHERE (f.requester.id = :userId OR f.receiver.id = :userId) AND f.status = :status")
    Page<Friendship> findFriendshipsByUserIdAndStatus(UUID userId, FriendshipStatus status, Pageable pageable);
}
