package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.EventDTO;
import vn.hust.social.backend.dto.EventParticipantDTO;
import vn.hust.social.backend.dto.event.CreateEventRequest;
import vn.hust.social.backend.dto.event.CreateEventResponse;
import vn.hust.social.backend.dto.event.GetEventParticipantsResponse;
import vn.hust.social.backend.dto.event.UpdateEventRequest;
import vn.hust.social.backend.dto.event.UpdateEventResponse;
import vn.hust.social.backend.dto.event.GetEventsResponse;
import jakarta.validation.constraints.Max;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import vn.hust.social.backend.security.JwtHeaderUtils;
import vn.hust.social.backend.security.JwtUtils;
import vn.hust.social.backend.service.event.EventService;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "Event management APIs")
@PreAuthorize("hasRole('STUDENT')")
public class EventController {
    private final EventService eventService;
    private final JwtUtils jwtUtils;

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Get detailed information of an event, including registration status of current user")
    public ResponseEntity<EventDTO> getEventById(@PathVariable UUID id, HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ResponseEntity.ok(eventService.getEventById(id, email));
    }

    @GetMapping
    @Operation(summary = "Get list of events", description = "Get paginated list of events. Supports sorting by createdAt, endTime, status (my registration status).")
    @Validated
    public ApiResponse<GetEventsResponse> getEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") @Max(50) int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(eventService.getEvents(page, pageSize, email));
    }

    @GetMapping("/search")
    @Operation(summary = "Search events by title")
    public ApiResponse<GetEventsResponse> searchEvents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(eventService.searchEvents(keyword, page, pageSize, email));
    }

    @PostMapping
    @Operation(summary = "Create an event", description = "Create a new event")
    public ApiResponse<CreateEventResponse> createEvent(
            @RequestParam UUID clubId,
            @RequestBody @Validated CreateEventRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(eventService.createEvent(clubId, request, email));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event", description = "Update an existing event")
    public ApiResponse<UpdateEventResponse> updateEvent(
            @PathVariable UUID id,
            @RequestBody @Validated UpdateEventRequest request,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        return ApiResponse.success(eventService.updateEvent(id, request, email));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event", description = "Delete an existing event")
    public ApiResponse<Void> deleteEvent(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        String email = JwtHeaderUtils.extractEmail(httpRequest, jwtUtils);
        eventService.deleteEvent(id, email);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join an event")
    public ApiResponse<Void> joinEvent(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        eventService.joinEvent(id, email);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/leave")
    @Operation(summary = "Leave an event")
    public ApiResponse<Void> leaveEvent(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        eventService.leaveEvent(id, email);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/participants/{userId}/approve")
    @Operation(summary = "Approve event participant request", description = "Approve a user's request to join an event (for private events)")
    public ApiResponse<Void> approveEventParticipant(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        eventService.approveEventParticipant(id, userId, email);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/participants/{userId}/reject")
    @Operation(summary = "Reject event participant request", description = "Reject a user's request to join an event (for private events)")
    public ApiResponse<Void> rejectEventParticipant(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        eventService.rejectEventParticipant(id, userId, email);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/participants")
    @Operation(summary = "Get event participants", description = "Get list of participants for an event")
    public ApiResponse<GetEventParticipantsResponse> getEventParticipants(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") @Max(50) int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(eventService.getEventParticipants(id, page, pageSize, email));
    }

    @GetMapping("/{id}/participants/{participantId}")
    @Operation(summary = "Get event participant by ID", description = "Get details of a specific participant")
    public ApiResponse<EventParticipantDTO> getEventParticipantById(
            @PathVariable UUID id,
            @PathVariable UUID participantId,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(eventService.getEventParticipantById(id, participantId, email));
    }

    @GetMapping("/{id}/participants/search")
    @Operation(summary = "Search event participants", description = "Search participants by name")
    public ApiResponse<GetEventParticipantsResponse> searchEventParticipants(
            @PathVariable UUID id,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") @Max(50) int pageSize,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        return ApiResponse.success(eventService.searchEventParticipants(id, keyword, page, pageSize, email));
    }
}
