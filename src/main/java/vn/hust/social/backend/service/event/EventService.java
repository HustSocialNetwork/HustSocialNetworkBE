package vn.hust.social.backend.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.dto.EventDTO;
import vn.hust.social.backend.dto.event.CreateEventRequest;
import vn.hust.social.backend.dto.event.CreateEventResponse;
import vn.hust.social.backend.dto.event.GetEventParticipantsResponse;
import vn.hust.social.backend.dto.event.UpdateEventRequest;
import vn.hust.social.backend.dto.event.UpdateEventResponse;

import vn.hust.social.backend.dto.event.GetEventsResponse;
import vn.hust.social.backend.dto.EventParticipantDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import vn.hust.social.backend.entity.club.Club;
import vn.hust.social.backend.entity.enums.club.ClubModeratorStatus;
import vn.hust.social.backend.entity.enums.event.EventType;
import vn.hust.social.backend.entity.enums.event.ParticipantStatus;
import vn.hust.social.backend.entity.event.Event;
import vn.hust.social.backend.entity.event.EventParticipant;
import vn.hust.social.backend.entity.user.User;
import vn.hust.social.backend.entity.user.UserAuth;
import vn.hust.social.backend.exception.ApiException;
import vn.hust.social.backend.mapper.EventMapper;
import vn.hust.social.backend.mapper.EventParticipantMapper;
import vn.hust.social.backend.repository.auth.UserAuthRepository;
import vn.hust.social.backend.repository.club.ClubModeratorRepository;
import vn.hust.social.backend.repository.club.ClubRepository;
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
        private final ClubRepository clubRepository;

        private final ClubModeratorRepository clubModeratorRepository;
        private final EventParticipantMapper eventParticipantMapper;

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

        @Transactional(readOnly = true)
        public GetEventsResponse searchEvents(String keyword, int page, int pageSize, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                UUID userId = userAuth.getUser().getId();

                Pageable pageable = PageRequest.of(page - 1, pageSize);
                Page<Event> eventsPage = eventRepository.searchByTitle(keyword, pageable);

                List<EventDTO> eventDTOs = eventsPage.getContent().stream().map(event -> {
                        ParticipantStatus status = eventParticipantRepository
                                        .findByEventIdAndUserId(event.getId(), userId)
                                        .map(EventParticipant::getStatus)
                                        .orElse(null);
                        EventDTO dto = eventMapper.toDTO(event);
                        dto.setMyRegistrationStatus(status);
                        return dto;
                }).collect(Collectors.toList());

                return new GetEventsResponse(eventDTOs);
        }

        @Transactional
        public CreateEventResponse createEvent(
                        UUID clubId,
                        CreateEventRequest request, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Club club = clubRepository.findById(clubId)
                                .orElseThrow(() -> new ApiException(ResponseCode.CLUB_NOT_FOUND));

                boolean isModerator = clubModeratorRepository.findByClubIdAndUserId(club.getId(), user.getId())
                                .map(m -> m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isModerator) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                if (request.startTime().isAfter(request.endTime())) {
                        throw new ApiException(ResponseCode.INVALID_EVENT_TIME);
                }

                Event event = new Event(club, request.title(), request.startTime(), request.endTime(), request.type(),
                                request.maxParticipants());
                event.setDescription(request.description());
                event.setLocation(request.location());
                if (request.bannerKey() != null) event.setBannerKey(request.bannerKey());

                event = eventRepository.save(event);

                return new CreateEventResponse(eventMapper.toDTO(event));
        }

        @Transactional
        public UpdateEventResponse updateEvent(UUID eventId,
                        UpdateEventRequest request, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_NOT_FOUND));

                boolean isModerator = clubModeratorRepository
                                .findByClubIdAndUserId(event.getClub().getId(), user.getId())
                                .map(m -> m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isModerator) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                if (request.startTime().isAfter(request.endTime())) {
                    throw new ApiException(ResponseCode.INVALID_EVENT_TIME);
                }

                if (request.title() != null)
                        event.setTitle(request.title());
                if (request.description() != null)
                        event.setDescription(request.description());
                if (request.startTime() != null)
                        event.setStartTime(request.startTime());
                if (request.endTime() != null)
                        event.setEndTime(request.endTime());
                if (request.location() != null)
                        event.setLocation(request.location());
                if (request.bannerKey() != null)
                        event.setBannerKey(request.bannerKey());
                if (request.type() != null)
                        event.setType(request.type());
                if (request.maxParticipants() != null)
                        event.setMaxParticipants(request.maxParticipants());
                if (request.status() != null)
                        event.setStatus(request.status());

                event = eventRepository.save(event);

                return new UpdateEventResponse(eventMapper.toDTO(event));
        }

        @Transactional
        public void deleteEvent(UUID eventId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_NOT_FOUND));

                boolean isModerator = clubModeratorRepository
                                .findByClubIdAndUserId(event.getClub().getId(), user.getId())
                                .map(m -> m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isModerator) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                eventRepository.delete(event);
        }

        @Transactional
        public void joinEvent(UUID eventId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_NOT_FOUND));

                if (eventParticipantRepository.existsByEventIdAndUserId(eventId, user.getId())) {
                        throw new ApiException(ResponseCode.USER_ALREADY_JOINED_EVENT);
                }

                if (event.getRegisteredCount() >= event.getMaxParticipants()) {
                        throw new ApiException(ResponseCode.EVENT_FULL);
                }

                if (event.getType() == EventType.PRIVATE) {
                        EventParticipant participant = new EventParticipant(event, user, ParticipantStatus.PENDING);
                        eventParticipantRepository.save(participant);
                } else {
                        EventParticipant participant = new EventParticipant(event, user, ParticipantStatus.ACCEPTED);
                        eventParticipantRepository.save(participant);

                        event.setRegisteredCount(event.getRegisteredCount() + 1);
                        eventRepository.save(event);
                }
        }

        @Transactional
        public void leaveEvent(UUID eventId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User user = userAuth.getUser();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_NOT_FOUND));

                EventParticipant participant = eventParticipantRepository.findByEventIdAndUserId(eventId, user.getId())
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_JOINED_EVENT));

                eventParticipantRepository.delete(participant);

                if (participant.getStatus() == ParticipantStatus.ACCEPTED) {
                        event.setRegisteredCount(event.getRegisteredCount() - 1);
                        eventRepository.save(event);
                }
        }

        @Transactional
        public void approveEventParticipant(UUID eventId, UUID userId, String moderatorEmail) {
                UserAuth moderatorAuth = userAuthRepository.findByEmail(moderatorEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User moderator = moderatorAuth.getUser();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_NOT_FOUND));

                boolean isModerator = clubModeratorRepository
                                .findByClubIdAndUserId(event.getClub().getId(), moderator.getId())
                                .map(m -> m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isModerator) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                EventParticipant participant = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_REGISTRATION_NOT_FOUND));

                if (participant.getStatus() == ParticipantStatus.ACCEPTED) {
                        throw new ApiException(ResponseCode.EVENT_PARTICIPANT_ALREADY_APPROVED);
                } else if (participant.getStatus() == ParticipantStatus.REJECTED) {
                        throw new ApiException(ResponseCode.EVENT_PARTICIPANT_ALREADY_REJECTED);
                }

                if (event.getRegisteredCount() >= event.getMaxParticipants()) {
                        throw new ApiException(ResponseCode.EVENT_FULL);
                }

                participant.setStatus(ParticipantStatus.ACCEPTED);
                eventParticipantRepository.save(participant);

                event.setRegisteredCount(event.getRegisteredCount() + 1);
                eventRepository.save(event);
        }

        @Transactional
        public void rejectEventParticipant(UUID eventId, UUID userId, String moderatorEmail) {
                UserAuth moderatorAuth = userAuthRepository.findByEmail(moderatorEmail)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
                User moderator = moderatorAuth.getUser();

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_NOT_FOUND));

                boolean isModerator = clubModeratorRepository
                                .findByClubIdAndUserId(event.getClub().getId(), moderator.getId())
                                .map(m -> m.getStatus() == ClubModeratorStatus.ACTIVE)
                                .orElse(false);
                if (!isModerator) {
                        throw new ApiException(ResponseCode.FORBIDDEN);
                }

                EventParticipant participant = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_REGISTRATION_NOT_FOUND));

                if (participant.getStatus() == ParticipantStatus.REJECTED) {
                        throw new ApiException(ResponseCode.EVENT_PARTICIPANT_ALREADY_REJECTED);
                }

                if (participant.getStatus() == ParticipantStatus.ACCEPTED) {
                        event.setRegisteredCount(event.getRegisteredCount() - 1);
                        eventRepository.save(event);
                }

                participant.setStatus(ParticipantStatus.REJECTED);
                eventParticipantRepository.save(participant);
        }

        @Transactional(readOnly = true)
        public GetEventParticipantsResponse getEventParticipants(UUID eventId, int page, int pageSize, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                // Ensure event exists
                if (!eventRepository.existsById(eventId)) {
                        throw new ApiException(ResponseCode.EVENT_NOT_FOUND);
                }

                Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("registeredAt").descending());
                Page<EventParticipant> participantsPage = eventParticipantRepository.findByEventId(eventId, pageable);

                List<EventParticipantDTO> dtos = participantsPage.getContent().stream()
                                .map(eventParticipantMapper::toDTO)
                                .toList();

                return new GetEventParticipantsResponse(dtos);
        }

        @Transactional(readOnly = true)
        public EventParticipantDTO getEventParticipantById(UUID eventId, UUID participantId, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                // Ensure event exists
                if (!eventRepository.existsById(eventId)) {
                        throw new ApiException(ResponseCode.EVENT_NOT_FOUND);
                }

                EventParticipant participant = eventParticipantRepository.findById(participantId)
                                .orElseThrow(() -> new ApiException(ResponseCode.EVENT_REGISTRATION_NOT_FOUND));

                if (!participant.getEvent().getId().equals(eventId)) {
                        throw new ApiException(ResponseCode.EVENT_REGISTRATION_NOT_FOUND);
                }

                return eventParticipantMapper.toDTO(participant);
        }

        @Transactional(readOnly = true)
        public GetEventParticipantsResponse searchEventParticipants(UUID eventId, String keyword, int page,
                        int pageSize, String email) {
                UserAuth userAuth = userAuthRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

                // Ensure event exists
                if (!eventRepository.existsById(eventId)) {
                        throw new ApiException(ResponseCode.EVENT_NOT_FOUND);
                }

                Pageable pageable = PageRequest.of(page - 1, pageSize);
                Page<EventParticipant> participantsPage = eventParticipantRepository.searchByEventIdAndKeyword(eventId,
                                keyword, pageable);

                List<EventParticipantDTO> dtos = participantsPage.getContent().stream()
                                .map(eventParticipantMapper::toDTO)
                                .toList();

                return new GetEventParticipantsResponse(dtos);
        }
}
