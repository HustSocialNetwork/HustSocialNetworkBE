package vn.hust.social.backend.repository.friendship;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hust.social.backend.entity.enums.user.FriendshipStatus;
import vn.hust.social.backend.entity.friendship.Friendship;
import vn.hust.social.backend.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findFriendshipByRequesterAndReceiverOrReceiverAndRequester(User requester, User receiver, User receiver1, User requester1);
    Optional<Friendship> findFriendshipById(UUID id);
    List<Friendship> findFriendshipsByReceiverAndStatus(User receiver, FriendshipStatus status);
    List<Friendship> findFriendshipsByRequesterAndStatus(User requester, FriendshipStatus status);
}
