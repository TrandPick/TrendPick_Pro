package project.trendpick_pro.domain.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationPeriod;
import project.trendpick_pro.domain.store.entity.Store;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    @Embedded
    private ExpirationPeriod expirationPeriod;

    private int limitCount;
    private int issueCount;

    private int discountPercent;
    private int minimumPurchaseAmount;

    private int limitIssueDate;

    @Builder
    private Coupon(String name, int limitCount, int limitIssueDate, int minimumPurchaseAmount, int discountPercent) {
        this.name = name;
        this.limitCount = limitCount;
        this.limitIssueDate = limitIssueDate;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.discountPercent = discountPercent;
        this.issueCount = 0;
    }

    public static Coupon of(StoreCouponSaveRequest request, String store) {
        return Coupon
                .builder()
                .name(createCouponName(store, request.getName()))
                .limitCount(request.getLimitCount())
                .limitIssueDate(request.getLimitIssueDate())
                .minimumPurchaseAmount(request.getMinimumPurchaseAmount())
                .discountPercent(request.getDiscountPercent())
                .build();
    }

    public void connectStore(Store store){
        this.store = store;
    }

    public boolean validateLimitCount(){
        return this.issueCount <= this.limitCount;
    }

    public boolean validateLimitIssueDate(LocalDateTime dateTime){
        return dateTime.isBefore(this.getCreatedDate().plusDays(this.limitIssueDate));
    }

    public boolean validateMinimumPurchaseAmount(int price){
        return this.minimumPurchaseAmount <= price;
    }

    public void increaseIssueCount(){
        this.issueCount++;
    }

    public void decreaseIssueCount(){
        this.issueCount--;
    }

    public void assignPeriodExpiration(LocalDateTime startDate, LocalDateTime endDate) {
        this.expirationPeriod = ExpirationPeriod.assignPeriod(startDate, endDate);
    }

    public void assignPostIssueExpiration(Integer issueAfterDate) {
        this.expirationPeriod = ExpirationPeriod.assignIssueAfterDate(issueAfterDate);
    }

    private static String createCouponName(String store, String requestName) {
        return "[" + store + "]" + " " + requestName;
    }
}
