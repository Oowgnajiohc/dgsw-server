package kr.hs.dgsw.course.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequest {

    @NotBlank(message = "refreshToken을 입력해주세요.")
    private String refreshToken;
}
