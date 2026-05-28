package kr.hs.dgsw.course.domain.member.service;

import kr.hs.dgsw.course.domain.member.dto.LoginRequest;
import kr.hs.dgsw.course.domain.member.dto.ReissueRequest;
import kr.hs.dgsw.course.domain.member.dto.SignupRequest;
import kr.hs.dgsw.course.domain.member.dto.TokenResponse;
import kr.hs.dgsw.course.domain.member.entity.Member;
import kr.hs.dgsw.course.domain.member.repository.MemberRepository;
import kr.hs.dgsw.course.global.exception.CustomException;
import kr.hs.dgsw.course.global.exception.ErrorCode;
import kr.hs.dgsw.course.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        memberRepository.save(Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build());
    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return issueTokens(member.getEmail());
    }

    public TokenResponse reissue(ReissueRequest request) {
        String refreshToken = request.getRefreshToken();
        tokenProvider.validateToken(refreshToken);

        String email = tokenProvider.getEmail(refreshToken);
        String stored = redisTemplate.opsForValue().get(REFRESH_PREFIX + email);

        if (stored == null || !stored.equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return issueTokens(email);
    }

    public void logout(String accessToken, String email) {
        redisTemplate.delete(REFRESH_PREFIX + email);

        long remaining = tokenProvider.getRemaining(accessToken);
        if (remaining > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "logout",
                    Duration.ofMillis(remaining)
            );
        }
    }

    private TokenResponse issueTokens(String email) {
        String accessToken = tokenProvider.createAccessToken(email);
        String refreshToken = tokenProvider.createRefreshToken(email);

        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + email,
                refreshToken,
                Duration.ofMillis(refreshTokenExpiration)
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}
