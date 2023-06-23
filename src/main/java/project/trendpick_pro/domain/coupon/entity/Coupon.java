package project.trendpick_pro.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationPeriod;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationType;
import project.trendpick_pro.domain.store.entity.Store;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store issuer;

    @Embedded
    private ExpirationPeriod expirationPeriod;
    @Column(name = "limit_count")
    private int limitCount;
    @Column(name = "issue_count")
    private int issueCount;

    @Column(name = "discount_percent")
    private int discountPercent;

    @Builder
    private Coupon(Store issuer, String name, int limitCount, int discountPercent, ExpirationPeriod expirationPeriod) {
        this.issuer = issuer;
        this.name = name;
        this.limitCount = limitCount;
        this.discountPercent = discountPercent;
        this.expirationPeriod = expirationPeriod;
        this.issueCount = 0;
    }

    public static Coupon issue(Store issuer, StoreCouponSaveRequest storeCouponSaveRequest) {
        Coupon coupon = Coupon
                .builder()
                .issuer(issuer)
                .name(storeCouponSaveRequest.getName())
                .limitCount(storeCouponSaveRequest.getLimitCount())
                .discountPercent(storeCouponSaveRequest.getDiscountPercent())
                .build();
        coupon.assignExpirationPeriod(storeCouponSaveRequest);

        return coupon;
    }

    private void assignExpirationPeriod(StoreCouponSaveRequest storeCouponSaveRequest) {
        if(storeCouponSaveRequest.getExpirationType().equals(ExpirationType.PERIOD.getValue()))
            this.expirationPeriod = ExpirationPeriod.assignPeriod(storeCouponSaveRequest.getStartDate(), storeCouponSaveRequest.getEndDate());
        else if(storeCouponSaveRequest.getExpirationType().equals(ExpirationType.ISSUE_AFTER_DATE.getValue()))
            this.expirationPeriod = ExpirationPeriod.assignIssueAfterDate(storeCouponSaveRequest.getIssueAfterDate());
    }

}
