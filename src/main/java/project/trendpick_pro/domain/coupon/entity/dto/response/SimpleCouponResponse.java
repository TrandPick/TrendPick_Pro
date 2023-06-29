package project.trendpick_pro.domain.coupon.entity.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.coupon.entity.Coupon;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SimpleCouponResponse {
    private Long couponId;
    private String couponName;
    private Integer minimumPurchaseAmount;

    private int discountPercent;
    private LocalDateTime issueDeadLine;

    private LocalDateTime couponCardStartDate;
    private LocalDateTime couponCardEndDate;
    private Integer issueAfterDate;
    private int limitCount;
    private int issueCount;

    public static SimpleCouponResponse of(Coupon coupon){
        return new SimpleCouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getMinimumPurchaseAmount(),
                coupon.getDiscountPercent(),
                coupon.getCreatedDate().plusDays(coupon.getLimitIssueDate()),
                coupon.getExpirationPeriod().getStartDate(),
                coupon.getExpirationPeriod().getEndDate(),
                coupon.getExpirationPeriod().getIssueAfterDate(),
                coupon.getLimitCount(),
                coupon.getIssueCount()
        );
    }


    public String getFormattedMinimumPurchaseAmount(){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(getMinimumPurchaseAmount())+"원";
    }
    public int getRemaining(){
        return getLimitCount() - getIssueCount();
    }

}
