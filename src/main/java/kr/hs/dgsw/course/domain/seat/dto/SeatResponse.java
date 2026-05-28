package kr.hs.dgsw.course.domain.seat.dto;

import kr.hs.dgsw.course.domain.seat.entity.Seat;
import kr.hs.dgsw.course.domain.seat.entity.SeatStatus;

public record SeatResponse(
        Long id,
        String grade,
        String seatNumber,
        int price,
        SeatStatus status
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getGrade(),
                seat.getSeatNumber(),
                seat.getPrice(),
                seat.getStatus()
        );
    }
}
