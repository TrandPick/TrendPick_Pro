package project.trendpick_pro.domain.member.entity.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record AccountForm(@NotBlank(message = "은행명을 입력해주세요.") String bankName,
                          @NotBlank(message = "계좌번호를 입력해주세요.") String bankAccount) {
}