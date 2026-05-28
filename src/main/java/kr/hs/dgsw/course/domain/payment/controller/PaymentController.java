package kr.hs.dgsw.course.domain.payment.controller;

import jakarta.validation.Valid;
import kr.hs.dgsw.course.domain.payment.dto.PaymentConfirmRequest;
import kr.hs.dgsw.course.domain.payment.dto.PaymentResponse;
import kr.hs.dgsw.course.domain.payment.service.PaymentService;
import kr.hs.dgsw.course.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ApiResponse<PaymentResponse> confirm(@Valid @RequestBody PaymentConfirmRequest request) {
        return ApiResponse.success(paymentService.confirm(request));
    }

    @PostMapping("/{paymentId}/cancel")
    public ApiResponse<PaymentResponse> cancel(@PathVariable Long paymentId) {
        return ApiResponse.success(paymentService.cancel(paymentId));
    }
}
