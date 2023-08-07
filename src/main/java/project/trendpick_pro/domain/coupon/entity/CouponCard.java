package project.trendpick_pro.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationPeriod;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponCard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon", nullable = false)
    private Coupon coupon;

    @Column(name = "coupon_code", nullable = false, unique = true)
    private UUID couponCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    private LocalDateTime usedDate;

    @Embedded
    @Column(nullable = false)
    private ExpirationPeriod expirationPeriod;

    public CouponCard(Coupon coupon){
        this.coupon = coupon;
        this.couponCode = UUID.randomUUID();
        this.coupon.increaseIssueCount();
    }

    public void updatePeriod(LocalDateTime dateTime){
        this.expirationPeriod = coupon.getExpirationPeriod();
        updateStatus(dateTime);
    }

    public void connectMember(Member member){
        this.member = member;
    }

    public boolean validate(OrderItem orderItem, LocalDateTime dateTime) {
        return validateExpirationPeriod(dateTime)
                && validateStatus()
                && validateMinimumPurchaseAmount(orderItem.getOrderPrice());
    }

    public void use(OrderItem orderItem, LocalDateTime dateTime) {
        settingStatusAndDate(dateTime);
        orderItem.applyCouponCard(this);
        orderItem.discount(
            orderItem.getOrderPrice() * (this.coupon.getDiscountPercent() / 100)
        );
    }

    public void cancel(OrderItem orderItem){
        this.status = CouponStatus.AVAILABLE;
        this.usedDate = null;
        orderItem.cancelCouponCard();
        this.coupon.decreaseIssueCount();
    }

    private void settingStatusAndDate(LocalDateTime dateTime) {
        this.status = CouponStatus.USED;
        this.usedDate = dateTime;
    }

    private boolean validateMinimumPurchaseAmount(int price){
        return this.coupon.validateMinimumPurchaseAmount(price);
    }

    private boolean validateExpirationPeriod(LocalDateTime dateTime){
        return dateTime.isAfter(getExpirationPeriod().getStartDate())
                && dateTime.isBefore(getExpirationPeriod().getEndDate());
    }

    private boolean validateStatus(){
        return getStatus().getValue().equals(CouponStatus.AVAILABLE.getValue());
    }

    private void updateStatus(LocalDateTime dateTime) {
        checkDateNotNull(dateTime);
        if(dateTime.isBefore(this.coupon.getExpirationPeriod().getStartDate())) {
            this.status = CouponStatus.NOT_YET_ACTIVE;
        } this.status = CouponStatus.AVAILABLE;
    }

    private void checkDateNotNull(LocalDateTime dateTime) {
        if (this.coupon.getExpirationPeriod().getStartDate() == null) {
            this.coupon.getExpirationPeriod().updateDate(dateTime);
        }
    }
}
