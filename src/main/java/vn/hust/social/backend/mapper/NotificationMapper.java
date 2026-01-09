package vn.hust.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.hust.social.backend.dto.notification.NotificationDTO;
import vn.hust.social.backend.entity.notification.Notification;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface NotificationMapper {

    @Mapping(target = "message", source = ".", qualifiedByName = "generateMessage")
    NotificationDTO toDTO(Notification notification);

    @Named("generateMessage")
    default String generateMessage(Notification notification) {
        String actorName = notification.getActor().getFullName();
        return switch (notification.getTargetType()) {
            case LIKE_POST -> actorName + " liked your post.";
            case LIKE_COMMENT -> actorName + " liked your comment.";
            case COMMENT_POST -> actorName + " commented on your post.";
            case REPLY_COMMENT -> actorName + " replied to your comment.";
            case FRIEND_REQUEST -> actorName + " sent you a friend request.";
            case ACCEPT_FRIEND -> actorName + " accepted your friend request.";
            case ASSIGN_CLUB_MODERATOR -> actorName + " assigned you as a moderator in the club.";
            case NEW_EVENT -> actorName + " created a new event in the club.";
            case EVENT_REGISTRATION_ACCEPTED -> actorName + " accepted your event registration.";
            case EVENT_REGISTRATION_REJECTED -> actorName + " rejected your event registration.";
        };
    }
}
