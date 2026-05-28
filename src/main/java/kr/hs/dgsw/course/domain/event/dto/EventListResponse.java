package kr.hs.dgsw.course.domain.event.dto;

import kr.hs.dgsw.course.domain.event.entity.Event;
import kr.hs.dgsw.course.domain.event.entity.EventCategory;

import java.time.LocalDateTime;

public record EventListResponse(
        Long id,
        String title,
        EventCategory category,
        String venue,
        LocalDateTime eventDate,
        String thumbnailUrl,
        int basePrice
) {
    public static EventListResponse from(Event event) {
        return new EventListResponse(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getVenue(),
                event.getEventDate(),
                event.getThumbnailUrl(),
                event.getBasePrice()
        );
    }
}
