package kr.hs.dgsw.course.domain.seat.repository;

import kr.hs.dgsw.course.domain.seat.entity.Seat;
import kr.hs.dgsw.course.domain.seat.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventId(Long eventId);

    List<Seat> findByStatus(SeatStatus status);
}
