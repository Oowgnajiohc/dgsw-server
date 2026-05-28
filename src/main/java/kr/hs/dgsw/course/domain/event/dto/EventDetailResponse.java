package kr.hs.dgsw.course.domain.event.dto;

import kr.hs.dgsw.course.domain.event.entity.Event;
import kr.hs.dgsw.course.domain.event.entity.EventCategory;

import java.time.LocalDateTime;

public record EventDetailResponse(
        Long id,
        String title,
        EventCategory category,
        String description,
        String venue,
        LocalDateTime eventDate,
        String thumbnailUrl,
        int totalSeats,
        int basePrice
) {
    public static EventDetailResponse from(Event event) {
        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getDescription(),
                event.getVenue(),
                event.getEventDate(),
                event.getThumbnailUrl(),
                event.getTotalSeats(),
                event.getBasePrice()
        );
    }
}
