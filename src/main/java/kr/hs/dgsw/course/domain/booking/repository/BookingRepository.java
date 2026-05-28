package kr.hs.dgsw.course.domain.booking.repository;

import kr.hs.dgsw.course.domain.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.items i JOIN FETCH i.seat WHERE b.member.id = :memberId ORDER BY b.createdAt DESC")
    List<Booking> findByMemberIdWithItems(@Param("memberId") Long memberId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.items i JOIN FETCH i.seat WHERE b.id = :id")
    Optional<Booking> findByIdWithItems(@Param("id") Long id);

    Optional<Booking> findByOrderId(String orderId);
}
