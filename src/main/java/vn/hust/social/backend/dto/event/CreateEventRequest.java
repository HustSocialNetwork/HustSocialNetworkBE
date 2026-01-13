package vn.hust.social.backend.dto.event;

import vn.hust.social.backend.entity.enums.event.EventType;

import java.time.Instant;

public record CreateEventRequest(
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        String location,
        String bannerKey,
        EventType type,
        Integer maxParticipants) {
}
