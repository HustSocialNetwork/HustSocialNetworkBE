package vn.hust.social.backend.dto.event;

import vn.hust.social.backend.dto.EventParticipantDTO;

import java.util.List;

public record GetEventParticipantsResponse(List<EventParticipantDTO> participants) {
}
