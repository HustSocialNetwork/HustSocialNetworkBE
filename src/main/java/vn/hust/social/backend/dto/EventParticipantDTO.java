package vn.hust.social.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hust.social.backend.entity.enums.event.ParticipantStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDTO {
    private UUID id;
    private UserDTO user;
    private ParticipantStatus status;
    private Instant registeredAt;
}
