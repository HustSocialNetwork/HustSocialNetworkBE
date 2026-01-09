package vn.hust.social.backend.service.target;

import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

public interface TargetStrategy {
    TargetType getTargetType();

    void validateView(User user, UUID targetId);

    void increaseLike(UUID targetId);

    void decreaseLike(UUID targetId);

    User getOwner(UUID targetId);
}
