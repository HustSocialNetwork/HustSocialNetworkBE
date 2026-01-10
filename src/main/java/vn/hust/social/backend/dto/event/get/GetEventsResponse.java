package vn.hust.social.backend.dto.event.get;

import vn.hust.social.backend.dto.EventDTO;
import java.util.List;

public record GetEventsResponse(
        List<EventDTO> events) {
}
