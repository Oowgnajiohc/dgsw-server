package kr.hs.dgsw.course.domain.member.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.course.domain.member.dto.LoginRequest;
import kr.hs.dgsw.course.domain.member.dto.ReissueRequest;
import kr.hs.dgsw.course.domain.member.dto.SignupRequest;
import kr.hs.dgsw.course.domain.member.dto.TokenResponse;
import kr.hs.dgsw.course.domain.member.service.AuthService;
import kr.hs.dgsw.course.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody ReissueRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.reissue(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestHeader("Authorization") String bearerToken,
            Authentication authentication
    ) {
        String accessToken = bearerToken.substring("Bearer ".length());
        authService.logout(accessToken, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
