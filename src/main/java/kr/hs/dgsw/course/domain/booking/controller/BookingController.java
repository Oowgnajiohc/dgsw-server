package kr.hs.dgsw.course.domain.booking.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.course.domain.booking.dto.BookingRequest;
import kr.hs.dgsw.course.domain.booking.dto.BookingResponse;
import kr.hs.dgsw.course.domain.booking.service.BookingService;
import kr.hs.dgsw.course.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingResponse> createBooking(
            Authentication authentication,
            @Valid @RequestBody BookingRequest request) {
        return ApiResponse.success(bookingService.createBooking(authentication.getName(), request));
    }

    @GetMapping
    public ApiResponse<List<BookingResponse>> getMyBookings(Authentication authentication) {
        return ApiResponse.success(bookingService.getMyBookings(authentication.getName()));
    }

    @DeleteMapping("/{bookingId}")
    public ApiResponse<Void> cancelBooking(
            Authentication authentication,
            @PathVariable Long bookingId) {
        bookingService.cancelBooking(authentication.getName(), bookingId);
        return ApiResponse.success(null);
    }
}
