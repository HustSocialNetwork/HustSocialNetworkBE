package vn.hust.social.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hust.social.backend.entity.enums.event.EventStatus;
import vn.hust.social.backend.entity.enums.event.EventType;
import vn.hust.social.backend.entity.enums.event.ParticipantStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private UUID id;
    private UUID clubId;
    private String clubName;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private String bannerKey;
    private EventStatus status;
    private EventType type;
    private Integer maxParticipants;
    private Integer registeredCount;
    private ParticipantStatus myRegistrationStatus;
}
