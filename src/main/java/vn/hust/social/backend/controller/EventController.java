package vn.hust.social.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.dto.EventDTO;
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
}
