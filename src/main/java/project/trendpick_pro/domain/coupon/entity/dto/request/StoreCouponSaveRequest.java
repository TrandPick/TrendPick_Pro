package project.trendpick_pro.domain.coupon.entity.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreCouponSaveRequest {

    @NotBlank(message = "쿠폰 이름을 입력해주세요.")
    private String name;
    @Min(value = 100, message = "적어도 100개 이상의 쿠폰을 발급하셔야 합니다.")
    private int limitCount;

    @Min(value = 1, message = "발급 가능 날짜는 적어도 하루 이상 되어야 합니다.")
    @Max(value = 365, message = "발급 가능 날짜는 최대 365일입니다.")
    private int limitIssueDate;
    private Integer minimumPurchaseAmount;
    @Min(value = 5, message = "할인률은 최소 5% 이상이어야 합니다.")
    @Max(value = 95, message = "할인률은 최대 95% 이하여야 합니다.")
    private int discountPercent;

    @NotBlank(message = "만료 타입을 설정해주셔야 합니다.")
    private String expirationType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer issueAfterDate;
}
