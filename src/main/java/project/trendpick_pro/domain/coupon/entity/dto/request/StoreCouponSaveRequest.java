package project.trendpick_pro.domain.coupon.entity.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StoreCouponSaveRequest {

    @NotBlank(message = "쿠폰 이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "제한 수량을 입력해주세요.")
    @Min(value = 100, message = "적어도 100개 이상의 쿠폰을 발급하셔야 합니다.")
    private int limitCount;
    @NotBlank(message = "할인률을 입력해주세요.")
    @Min(value = 5, message = "할인률은 최소 5% 이상이어야 합니다.")
    @Max(value = 95, message = "할인률은 최대 95% 이하여야 합니다.")
    private int discountPercent;

    @NotNull(message = "시작 날짜를 정해주세요.")
    private LocalDateTime startDate;
    @NotNull(message = "마감 날짜를 정해주세요.")
    private LocalDateTime endDate;
}
