package kr.hs.dgsw.course.domain.booking.entity;

import jakarta.persistence.*;
import kr.hs.dgsw.course.domain.seat.entity.Seat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking_item")
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private int price;

    @Builder
    public BookingItem(Booking booking, Seat seat, int price) {
        this.booking = booking;
        this.seat = seat;
        this.price = price;
    }
}
