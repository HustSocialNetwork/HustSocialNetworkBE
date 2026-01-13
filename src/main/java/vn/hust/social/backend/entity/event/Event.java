package vn.hust.social.backend.entity.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hust.social.backend.entity.Base;
import vn.hust.social.backend.entity.club.Club;
import vn.hust.social.backend.entity.enums.event.EventStatus;
import vn.hust.social.backend.entity.enums.event.EventType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false, columnDefinition = "BINARY(16)")
    private Club club;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "location")
    private String location;

    @Column(name = "banner_key")
    private String bannerKey = "/event-banners/default.png";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status = EventStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EventType type;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "registered_count", nullable = false)
    private Integer registeredCount = 0;

    protected Event() {
    }

    public Event(Club club, String title, Instant startTime, Instant endTime, EventType type, Integer maxParticipants) {
        this.club = club;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.maxParticipants = maxParticipants;
        this.status = EventStatus.UPCOMING;
        this.registeredCount = 0;
    }
}
