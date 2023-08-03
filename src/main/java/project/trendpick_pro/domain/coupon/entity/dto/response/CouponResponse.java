package project.trendpick_pro.domain.coupon.entity.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.coupon.entity.Coupon;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@NoArgsConstructor
public class CouponResponse {

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

    @Builder
    private CouponResponse(Long couponId, String couponName, Integer minimumPurchaseAmount,
                          int discountPercent, LocalDateTime issueDeadLine, LocalDateTime couponCardStartDate,
                          LocalDateTime couponCardEndDate, Integer issueAfterDate, int limitCount, int issueCount) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.discountPercent = discountPercent;
        this.issueDeadLine = issueDeadLine;
        this.couponCardStartDate = couponCardStartDate;
        this.couponCardEndDate = couponCardEndDate;
        this.issueAfterDate = issueAfterDate;
        this.limitCount = limitCount;
        this.issueCount = issueCount;
    }

    public static CouponResponse of(Coupon coupon){
        return CouponResponse.builder()
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .minimumPurchaseAmount(coupon.getMinimumPurchaseAmount())
                .discountPercent(coupon.getDiscountPercent())
                .issueDeadLine(coupon.getCreatedDate().plusDays(coupon.getLimitIssueDate()))
                .couponCardStartDate(coupon.getExpirationPeriod().getStartDate())
                .couponCardEndDate(coupon.getExpirationPeriod().getEndDate())
                .issueAfterDate(coupon.getExpirationPeriod().getIssueAfterDate())
                .limitCount(coupon.getLimitCount())
                .issueCount(coupon.getIssueCount())
                .build();
    }

    public String getFormattedMinimumPurchaseAmount(){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(getMinimumPurchaseAmount())+"원";
    }

    public int getRemaining(){
        return getLimitCount() - getIssueCount();
    }
}
