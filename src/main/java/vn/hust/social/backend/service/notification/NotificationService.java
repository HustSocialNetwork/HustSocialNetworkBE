package vn.hust.social.backend.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.common.response.WsResponse;
import vn.hust.social.backend.dto.NotificationDTO;
import vn.hust.social.backend.dto.notification.GetNotificationsResponse;
import vn.hust.social.backend.entity.enums.notification.NotificationType;
import vn.hust.social.backend.entity.notification.Notification;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.NotificationMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.notification.NotificationRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserAuthRepository userAuthRepository;

    @Transactional
    public void sendNotification(User recipient, User actor, NotificationType targetType, UUID targetId) {
        if (recipient.getId().equals(actor.getId())) {
            return;
        }

        Notification notification = new Notification(recipient, actor, targetType, targetId);

        notification = notificationRepository.save(notification);
        NotificationDTO dto = notificationMapper.toDTO(notification);

        userAuthRepository.findByUserId(recipient.getId()).forEach(userAuth -> messagingTemplate.convertAndSendToUser(
                userAuth.getEmail(),
                "/queue/notifications",
                WsResponse.success(dto)));
    }

    @Transactional(readOnly = true)
    public GetNotificationsResponse getNotifications(String email, int page, int size) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User recipient = userAuth.getUser();

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDTO> notificationPage = notificationRepository
                .findByRecipientOrderByCreatedAtDesc(recipient, pageable)
                .map(notificationMapper::toDTO);

        return new GetNotificationsResponse(
                notificationPage.getContent(),
                notificationPage.getTotalElements(),
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages());
    }

    @Transactional
    public void markAsRead(UUID notificationId, String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User user = userAuth.getUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(ResponseCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ApiException(ResponseCode.FORBIDDEN);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String email) {
        UserAuth userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        User recipient = userAuth.getUser();

        notificationRepository.markAllAsRead(recipient);
    }
}
