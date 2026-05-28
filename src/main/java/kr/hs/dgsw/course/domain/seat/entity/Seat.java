package kr.hs.dgsw.course.domain.seat.entity;

import jakarta.persistence.*;
import kr.hs.dgsw.course.domain.event.entity.Event;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Builder
    public Seat(Event event, String grade, String seatNumber, int price) {
        this.event = event;
        this.grade = grade;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    public void changeStatus(SeatStatus status) {
        this.status = status;
    }
}
