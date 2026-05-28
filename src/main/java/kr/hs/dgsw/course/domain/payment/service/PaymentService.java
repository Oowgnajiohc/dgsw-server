package kr.hs.dgsw.course.domain.payment.service;

import kr.hs.dgsw.course.domain.booking.entity.Booking;
import kr.hs.dgsw.course.domain.booking.entity.BookingStatus;
import kr.hs.dgsw.course.domain.booking.repository.BookingRepository;
import kr.hs.dgsw.course.domain.payment.dto.PaymentConfirmRequest;
import kr.hs.dgsw.course.domain.payment.dto.PaymentResponse;
import kr.hs.dgsw.course.domain.payment.entity.Payment;
import kr.hs.dgsw.course.domain.payment.repository.PaymentRepository;
import kr.hs.dgsw.course.domain.seat.service.SeatService;
import kr.hs.dgsw.course.global.exception.CustomException;
import kr.hs.dgsw.course.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final SeatService seatService;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Value("${toss.confirm-url}")
    private String tossConfirmUrl;

    public PaymentResponse confirm(PaymentConfirmRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getTotalAmount() != request.amount()) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        callTossConfirmApi(request.paymentKey(), request.orderId(), request.amount());

        booking.confirm();
        booking.getItems().forEach(item -> seatService.confirmSeat(item.getSeat().getId()));

        Payment payment = Payment.builder()
                .booking(booking)
                .paymentKey(request.paymentKey())
                .amount(request.amount())
                .build();

        paymentRepository.save(payment);
        return PaymentResponse.from(payment);
    }

    public PaymentResponse cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        callTossCancelApi(payment.getPaymentKey());

        payment.cancel();
        payment.getBooking().cancel();
        payment.getBooking().getItems().forEach(item -> seatService.releaseSeat(item.getSeat().getId()));

        return PaymentResponse.from(payment);
    }

    private void callTossConfirmApi(String paymentKey, String orderId, int amount) {
        String encoded = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        try {
            RestClient.create().post()
                    .uri(tossConfirmUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    private void callTossCancelApi(String paymentKey) {
        String encoded = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        try {
            RestClient.create().post()
                    .uri("https://api.tosspayments.com/v1/payments/{paymentKey}/cancel", paymentKey)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cancelReason", "사용자 요청"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }
}
