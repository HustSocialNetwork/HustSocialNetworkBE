package vn.hust.social.backend.entity.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.enums.event.ParticipantStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_participant", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "event_id", "user_id" })
})
@Getter
@Setter
public class EventParticipant extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, columnDefinition = "BINARY(16)")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private User user;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status = ParticipantStatus.PENDING;

    protected EventParticipant() {
    }

    public EventParticipant(Event event, User user, ParticipantStatus status) {
        this.event = event;
        this.user = user;
        this.status = status;
        this.registeredAt = Instant.now();
    }
}
