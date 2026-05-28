package kr.hs.dgsw.course.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "M002", "이미 사용중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "M003", "비밀번호가 일치하지 않습니다."),

    // JWT
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "J001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "J002", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "J003", "RefreshToken을 찾을 수 없습니다."),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "J004", "로그아웃된 토큰입니다."),

    // Event
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "이벤트를 찾을 수 없습니다."),

    // Seat
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "좌석을 찾을 수 없습니다."),
    SEAT_NOT_AVAILABLE(HttpStatus.CONFLICT, "S002", "선택할 수 없는 좌석입니다."),

    // Booking
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "예매를 찾을 수 없습니다."),
    BOOKING_ACCESS_DENIED(HttpStatus.FORBIDDEN, "B002", "예매에 접근 권한이 없습니다."),
    BOOKING_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "B003", "취소할 수 없는 예매입니다."),

    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "결제를 찾을 수 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "P002", "결제 금액이 일치하지 않습니다."),
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "P003", "결제 승인에 실패했습니다."),
    PAYMENT_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "P004", "결제 환불에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
