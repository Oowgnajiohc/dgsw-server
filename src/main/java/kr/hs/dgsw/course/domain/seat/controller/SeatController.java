package kr.hs.dgsw.course.domain.seat.controller;

import kr.hs.dgsw.course.domain.seat.dto.SeatResponse;
import kr.hs.dgsw.course.domain.seat.service.SeatService;
import kr.hs.dgsw.course.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success(seatService.getSeats(eventId)));
    }
}
