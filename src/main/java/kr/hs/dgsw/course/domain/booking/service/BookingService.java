package kr.hs.dgsw.course.domain.booking.service;

import kr.hs.dgsw.course.domain.booking.dto.BookingRequest;
import kr.hs.dgsw.course.domain.booking.dto.BookingResponse;
import kr.hs.dgsw.course.domain.booking.entity.Booking;
import kr.hs.dgsw.course.domain.booking.entity.BookingItem;
import kr.hs.dgsw.course.domain.booking.entity.BookingStatus;
import kr.hs.dgsw.course.domain.booking.repository.BookingRepository;
import kr.hs.dgsw.course.domain.member.entity.Member;
import kr.hs.dgsw.course.domain.member.repository.MemberRepository;
import kr.hs.dgsw.course.domain.seat.entity.Seat;
import kr.hs.dgsw.course.domain.seat.service.SeatService;
import kr.hs.dgsw.course.global.exception.CustomException;
import kr.hs.dgsw.course.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MemberRepository memberRepository;
    private final SeatService seatService;

    public BookingResponse createBooking(String memberEmail, BookingRequest request) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Seat> seats = request.seatIds().stream()
                .map(seatService::holdSeat)
                .toList();

        int totalAmount = seats.stream().mapToInt(Seat::getPrice).sum();
        String orderId = UUID.randomUUID().toString();

        Booking booking = Booking.builder()
                .member(member)
                .orderId(orderId)
                .totalAmount(totalAmount)
                .build();

        seats.forEach(seat -> {
            BookingItem item = BookingItem.builder()
                    .booking(booking)
                    .seat(seat)
                    .price(seat.getPrice())
                    .build();
            booking.addItem(item);
        });

        bookingRepository.save(booking);
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return bookingRepository.findByMemberIdWithItems(member.getId()).stream()
                .map(BookingResponse::from)
                .toList();
    }

    public void cancelBooking(String memberEmail, Long bookingId) {
        Booking booking = bookingRepository.findByIdWithItems(bookingId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getMember().getEmail().equals(memberEmail)) {
            throw new CustomException(ErrorCode.BOOKING_ACCESS_DENIED);
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException(ErrorCode.BOOKING_CANCEL_NOT_ALLOWED);
        }

        booking.getItems().forEach(item -> seatService.releaseSeat(item.getSeat().getId()));
        booking.cancel();
    }
}
