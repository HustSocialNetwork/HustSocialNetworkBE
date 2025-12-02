package vn.hust.social.backend.service.user.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.entity.enums.user.FriendshipStatus;
import vn.hust.social.backend.entity.user.Friendship;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.repository.user.FriendshipRepository;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public boolean isFriend(User user1, User user2) {
        Friendship friendship = friendshipRepository.findByUser1IdAndUser2IdOrUser1IdAndUser2Id(user1.getId(), user2.getId(), user2.getId(), user1.getId()).orElse(null);
        return friendship.getStatus() == FriendshipStatus.ACCEPTED;
    }
}
