package kr.hs.dgsw.course.domain.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull Long eventId,
        @NotEmpty List<Long> seatIds
) {}
