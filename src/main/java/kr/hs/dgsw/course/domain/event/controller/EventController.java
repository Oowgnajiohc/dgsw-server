package kr.hs.dgsw.course.domain.event.controller;

import kr.hs.dgsw.course.domain.event.dto.EventDetailResponse;
import kr.hs.dgsw.course.domain.event.dto.EventListResponse;
import kr.hs.dgsw.course.domain.event.entity.EventCategory;
import kr.hs.dgsw.course.domain.event.service.EventService;
import kr.hs.dgsw.course.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventListResponse>>> getEvents(
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getEvents(category, date)));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDetailResponse>> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getEvent(eventId)));
    }
}
