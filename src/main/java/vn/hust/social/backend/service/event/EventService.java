package vn.hust.social.backend.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.EventDTO;
import vn.hust.social.backend.dto.event.get.GetEventsResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import vn.hust.social.backend.entity.enums.event.ParticipantStatus;
import vn.hust.social.backend.entity.event.Event;
import vn.hust.social.backend.entity.event.EventParticipant;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.EventMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.event.EventParticipantRepository;
import vn.hust.social.backend.repository.event.EventRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
        private final EventRepository eventRepository;
        private final EventMapper eventMapper;
        private final UserAuthRepository userAuthRepository;
        private final EventParticipantRepository eventParticipantRepository;

        @Transactional(readOnly = true)
        public EventDTO getEventById(UUID eventId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(
                                                ResponseCode.USER_NOT_FOUND));
                UUID userId = userAuth.getUser().getId();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(
                                                ResponseCode.EVENT_NOT_FOUND));

                ParticipantStatus status = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                                .map(EventParticipant::getStatus)
                                .orElse(null);

                EventDTO dto = eventMapper.toDTO(event);
                dto.setMyRegistrationStatus(status);
                return dto;
        }

        @Transactional(readOnly = true)
        public GetEventsResponse getEvents(int page, int pageSize, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(
                                                ResponseCode.USER_NOT_FOUND));
                UUID userId = userAuth.getUser().getId();

                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("startTime").descending());

                Page<Event> events = eventRepository.findAll(pageable);

                if (events.isEmpty()) {
                        return new GetEventsResponse(List.of());
                }

                List<UUID> eventIds = events.getContent().stream()
                                .map(Event::getId)
                                .toList();

                Map<UUID, ParticipantStatus> statusMap = eventParticipantRepository
                                .findByUserIdAndEventIdIn(userId, eventIds)
                                .stream()
                                .collect(Collectors.toMap(
                                                participant -> participant.getEvent().getId(),
                                                EventParticipant::getStatus));

                List<EventDTO> eventDTOs = events.getContent().stream()
                                .map(event -> {
                                        EventDTO dto = eventMapper.toDTO(event);
                                        dto.setMyRegistrationStatus(statusMap.get(event.getId()));
                                        return dto;
                                })
                                .toList();

                return new GetEventsResponse(eventDTOs);
        }
}
