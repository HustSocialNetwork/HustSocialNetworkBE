package vn.hust.social.backend.service.target;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.post.Post;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.post.PostRepository;
import vn.hust.social.backend.service.post.PostPermissionService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostTargetStrategy implements TargetStrategy {
    private final PostRepository postRepository;
    private final PostPermissionService postPermissionService;

    @Override
    public TargetType getTargetType() {
        return TargetType.POST;
    }

    @Override
    public void validateView(User user, UUID targetId) {
        Post post = postRepository.findById(targetId).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        if (!postPermissionService.canViewPost(user, post)) {
            throw new ApiException(ResponseCode.CANNOT_VIEW_POST);
        }
    }

    @Override
    public void increaseLike(UUID targetId) {
        Post post = postRepository.findById(targetId).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
    }

    @Override
    public void decreaseLike(UUID targetId) {
        Post post = postRepository.findById(targetId).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        post.setLikesCount(post.getLikesCount() - 1);
        postRepository.save(post);
    }

    @Override
    public User getOwner(UUID targetId) {
        Post post = postRepository.findById(targetId).orElseThrow(() -> new ApiException(ResponseCode.POST_NOT_FOUND));
        return post.getUser();
    }
}
