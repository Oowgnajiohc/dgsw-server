package kr.hs.dgsw.course.domain.payment.dto;

import kr.hs.dgsw.course.domain.payment.entity.Payment;
import kr.hs.dgsw.course.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long bookingId,
        String paymentKey,
        int amount,
        PaymentStatus status,
        LocalDateTime approvedAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getApprovedAt()
        );
    }
}
