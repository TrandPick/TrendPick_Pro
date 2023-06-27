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

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    @Embedded
    @Column(nullable = false)
    private ExpirationPeriod expirationPeriod; //쿠폰 유효기한
    @Column(name = "limit_count")
    private int limitCount;
    @Column(name = "issue_count")
    private int issueCount;

    @Column(name = "discount_percent", nullable = false)
    private int discountPercent;

    @Column(name = "limit_issue_date", nullable = false)
    private int limitIssueDate;

    @Column(name = "minimum_purchase_amount")
    private Integer minimumPurchaseAmount;

    @Builder
    private Coupon(Store store, String name, int limitCount, int limitIssueDate,Integer minimumPurchaseAmount, int discountPercent, ExpirationPeriod expirationPeriod) {
        this.store = store;
        this.name = "["+store.getBrand()+"]"+" "+name;
        this.limitCount = limitCount;
        this.limitIssueDate = limitIssueDate;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.discountPercent = discountPercent;
        this.expirationPeriod = expirationPeriod;
        this.issueCount = 0;
    }

    public static Coupon generate(Store store, StoreCouponSaveRequest storeCouponSaveRequest) {
        Coupon coupon = Coupon
                .builder()
                .store(store)
                .name(storeCouponSaveRequest.getName())
                .limitCount(storeCouponSaveRequest.getLimitCount())
                .limitIssueDate(storeCouponSaveRequest.getLimitIssueDate())
                .minimumPurchaseAmount(storeCouponSaveRequest.getMinimumPurchaseAmount())
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

    public boolean validateLimitCount(){
        return this.issueCount <= this.limitCount;
    }

    public boolean validateLimitIssueDate(){
        return LocalDateTime.now().isBefore(this.getCreatedDate().plusDays(this.limitIssueDate));
    }

    public boolean validateMinimumPurchaseAmount(int price){
        return this.minimumPurchaseAmount <= price;
    }

    public void increaseIssueCount(){
        this.issueCount = getIssueCount() + 1;
    }
}
