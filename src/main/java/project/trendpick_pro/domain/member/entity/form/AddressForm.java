package project.trendpick_pro.domain.member.entity.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddressForm(@NotBlank(message = "주소를 입력해주세요.") String address) {
}