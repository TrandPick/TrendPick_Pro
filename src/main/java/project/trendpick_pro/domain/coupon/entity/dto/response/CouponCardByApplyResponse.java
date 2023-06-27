package project.trendpick_pro.domain.coupon.entity.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.orders.entity.OrderItem;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponCardByApplyResponse {
    private Long couponCardId;
    private int discountPercent;
    private String couponName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int discountPrice;

    public static CouponCardByApplyResponse of(CouponCard couponCard, OrderItem orderItem) {
        return new CouponCardByApplyResponse(
                couponCard.getId(),
                couponCard.getCoupon().getDiscountPercent(),
                couponCard.getCoupon().getName(),
                couponCard.getExpirationPeriod().getStartDate(),
                couponCard.getExpirationPeriod().getEndDate(),
                orderItem.getOrderPrice() * couponCard.getCoupon().getDiscountPercent() / 100)
                ;
    }

    public String getFormattedDiscountPrice() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(getDiscountPrice())+"원";
    }

}
