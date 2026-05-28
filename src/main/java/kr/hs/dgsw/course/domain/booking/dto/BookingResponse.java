package kr.hs.dgsw.course.domain.booking.dto;

import kr.hs.dgsw.course.domain.booking.entity.Booking;
import kr.hs.dgsw.course.domain.booking.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String orderId,
        BookingStatus status,
        int totalAmount,
        LocalDateTime createdAt,
        String eventTitle,
        List<BookingItemResponse> items
) {
    public static BookingResponse from(Booking booking) {
        String eventTitle = booking.getItems().isEmpty() ? null
                : booking.getItems().get(0).getSeat().getEvent().getTitle();
        return new BookingResponse(
                booking.getId(),
                booking.getOrderId(),
                booking.getStatus(),
                booking.getTotalAmount(),
                booking.getCreatedAt(),
                eventTitle,
                booking.getItems().stream().map(BookingItemResponse::from).toList()
        );
    }
}
