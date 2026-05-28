package kr.hs.dgsw.course.domain.payment.entity;

import jakarta.persistence.*;
import kr.hs.dgsw.course.domain.booking.entity.Booking;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Builder
    public Payment(Booking booking, String paymentKey, int amount) {
        this.booking = booking;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = PaymentStatus.DONE;
        this.approvedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }
}
