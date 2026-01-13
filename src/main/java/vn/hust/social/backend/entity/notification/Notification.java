package vn.hust.social.backend.entity.notification;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.enums.notification.NotificationType;
import vn.hust.social.backend.entity.user.User;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "notification", indexes = {
        @Index(name = "idx_notification_recipient_id", columnList = "recipient_id"),
        @Index(name = "idx_notification_recipient_id_is_read", columnList = "recipient_id, is_read")
})
@Entity
public class Notification extends Base {

    protected Notification() {
    }

    public Notification(User recipient, User actor, NotificationType targetType, UUID targetId) {
        this.recipient = recipient;
        this.actor = actor;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private NotificationType targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "is_read")
    private boolean read = false;
}
