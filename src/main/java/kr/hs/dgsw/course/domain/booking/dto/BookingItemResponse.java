package kr.hs.dgsw.course.domain.booking.dto;

import kr.hs.dgsw.course.domain.booking.entity.BookingItem;

import java.time.LocalDateTime;

public record BookingItemResponse(
        Long seatId,
        String grade,
        String seatNumber,
        int price,
        String eventTitle,
        String venue,
        LocalDateTime eventDate
) {
    public static BookingItemResponse from(BookingItem item) {
        var event = item.getSeat().getEvent();
        return new BookingItemResponse(
                item.getSeat().getId(),
                item.getSeat().getGrade(),
                item.getSeat().getSeatNumber(),
                item.getPrice(),
                event.getTitle(),
                event.getVenue(),
                event.getEventDate()
        );
    }
}
