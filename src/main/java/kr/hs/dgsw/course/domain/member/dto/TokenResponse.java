package kr.hs.dgsw.course.domain.member.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
