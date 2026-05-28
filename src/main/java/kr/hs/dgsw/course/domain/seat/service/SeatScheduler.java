package kr.hs.dgsw.course.domain.seat.service;

import kr.hs.dgsw.course.domain.seat.entity.Seat;
import kr.hs.dgsw.course.domain.seat.entity.SeatStatus;
import kr.hs.dgsw.course.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private static final String SEAT_HELD_PREFIX = "seat:held:";

    private final SeatRepository seatRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 1분마다 TTL 만료된 HELD 좌석 AVAILABLE로 복귀
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void releaseExpiredHeldSeats() {
        List<Seat> heldSeats = seatRepository.findByStatus(SeatStatus.HELD);

        List<Seat> expired = heldSeats.stream()
                .filter(seat -> !Boolean.TRUE.equals(
                        redisTemplate.hasKey(SEAT_HELD_PREFIX + seat.getId())))
                .toList();

        expired.forEach(seat -> seat.changeStatus(SeatStatus.AVAILABLE));

        if (!expired.isEmpty()) {
            log.info("만료된 임시 점유 좌석 {}개 복귀 처리", expired.size());
        }
    }
}
