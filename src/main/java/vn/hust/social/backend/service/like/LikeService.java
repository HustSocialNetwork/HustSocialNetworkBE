package vn.hust.social.backend.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.like.Like;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.like.LikeRepository;
import vn.hust.social.backend.entity.enums.notification.NotificationType;
import vn.hust.social.backend.service.notification.NotificationService;
import vn.hust.social.backend.service.target.TargetStrategy;
import vn.hust.social.backend.service.target.TargetStrategyResolver;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserAuthRepository userAuthRepository;
    private final TargetStrategyResolver strategyResolver;
    private final NotificationService notificationService;

    @Transactional
    public void like(UUID targetId, TargetType targetType, String email) {
        User user = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND))
                .getUser();

        if (likeRepository.existsByUserIdAndTargetIdAndTargetType(user.getId(), targetId, targetType)) {
            throw new ApiException(ResponseCode.ALREADY_LIKED);
        }

        TargetStrategy strategy = strategyResolver.resolve(targetType);

        strategy.validateView(user, targetId);
        strategy.increaseLike(targetId);

        likeRepository.save(new Like(user, targetId, targetType));

        User recipient = strategy.getOwner(targetId);
        NotificationType notificationType = targetType == TargetType.POST ? NotificationType.LIKE_POST
                : NotificationType.LIKE_COMMENT;
        notificationService.sendNotification(recipient, user, notificationType, targetId);
    }

    @Transactional
    public void unlike(UUID targetId, TargetType targetType, String email) {
        User user = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND))
                .getUser();

        Like like = likeRepository.findByUserIdAndTargetIdAndTargetType(user.getId(), targetId, targetType)
                .orElseThrow(() -> new ApiException(ResponseCode.ALREADY_UNLIKED));

        TargetStrategy strategy = strategyResolver.resolve(targetType);

        strategy.validateView(user, targetId);
        strategy.decreaseLike(targetId);

        likeRepository.delete(like);
    }
}
