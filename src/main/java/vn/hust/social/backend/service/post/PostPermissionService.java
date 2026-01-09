package vn.hust.social.backend.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.enums.post.PostVisibility;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.repository.block.BlockRepository;
import vn.hust.social.backend.service.friendship.FriendshipService;

@Service
@RequiredArgsConstructor
public class PostPermissionService {

    private final BlockRepository blockRepository;
    private final FriendshipService friendshipService;

    public boolean canViewPost(User viewer, Post post) {
        if (blockRepository.existsByBlockerIdAndBlockedId(viewer.getId(), post.getUser().getId())) {
            return false;
        }

        if (blockRepository.existsByBlockerIdAndBlockedId(post.getUser().getId(), viewer.getId())) {
            return false;
        }

        if (post.getVisibility() == PostVisibility.PUBLIC)
            return true;

        if (post.getUser().getId().equals(viewer.getId()))
            return true;

        if (post.getVisibility() == PostVisibility.FRIENDS) {
            return friendshipService.isFriend(viewer.getId(), post.getUser().getId());
        }

        return false;
    }
}
