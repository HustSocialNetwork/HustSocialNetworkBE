package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.EventDTO;
import vn.hust.social.backend.dto.event.CreateEventRequest;
import vn.hust.social.backend.dto.event.CreateEventResponse;
import vn.hust.social.backend.dto.event.UpdateEventRequest;
import vn.hust.social.backend.dto.event.UpdateEventResponse;
import vn.hust.social.backend.dto.event.get.GetEventsResponse;
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

    @PostMapping("/{id}/join")
    @Operation(summary = "Join an event")
    public ApiResponse<Void> joinEvent(
            @PathVariable UUID id,
            HttpServletRequest request) {
        String email = JwtHeaderUtils.extractEmail(request, jwtUtils);
        eventService.joinEvent(id, email);
        return ApiResponse.success(null);
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
}
