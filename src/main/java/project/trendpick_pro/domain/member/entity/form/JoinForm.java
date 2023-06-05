package project.trendpick_pro.domain.member.entity.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record JoinForm(@NotBlank(message = "email을 입력해주세요.") String email,
                       @NotBlank(message = "password를 입력해주세요.") String password,
                       @NotBlank(message = "이름을 입력해주세요.") String username,
                       @NotBlank(message = "휴대폰 번호를 입력해주세요.") String phoneNumber,
                       @NotBlank(message = "권한을 입력해주세요.") String state,
                       @NotEmpty(message = "선호하는 태그를 입력해주세요.")List<String> tags) {
}