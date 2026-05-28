package kr.hs.dgsw.course.domain.seat.service;

import kr.hs.dgsw.course.domain.seat.dto.SeatResponse;
import kr.hs.dgsw.course.domain.seat.entity.Seat;
import kr.hs.dgsw.course.domain.seat.entity.SeatStatus;
import kr.hs.dgsw.course.domain.seat.repository.SeatRepository;
import kr.hs.dgsw.course.global.exception.CustomException;
import kr.hs.dgsw.course.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private static final String SEAT_HELD_PREFIX = "seat:held:";
    private static final Duration HOLD_TTL = Duration.ofMinutes(10);

    private final SeatRepository seatRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeats(Long eventId) {
        return seatRepository.findByEventId(eventId).stream()
                .map(SeatResponse::from)
                .toList();
    }

    public Seat holdSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new CustomException(ErrorCode.SEAT_NOT_AVAILABLE);
        }

        seat.changeStatus(SeatStatus.HELD);
        redisTemplate.opsForValue().set(SEAT_HELD_PREFIX + seatId, "held", HOLD_TTL);
        return seat;
    }

    public void releaseSeat(Long seatId) {
        seatRepository.findById(seatId).ifPresent(seat -> {
            seat.changeStatus(SeatStatus.AVAILABLE);
            redisTemplate.delete(SEAT_HELD_PREFIX + seatId);
        });
    }

    public void confirmSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
        seat.changeStatus(SeatStatus.BOOKED);
        redisTemplate.delete(SEAT_HELD_PREFIX + seatId);
    }
}
