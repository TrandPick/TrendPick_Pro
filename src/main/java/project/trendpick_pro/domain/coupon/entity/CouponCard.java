package project.trendpick_pro.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
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

    @Column(name = "used_date")
    private LocalDateTime usedDate;

    @Column(name = "order_item_id", nullable = true)
    private Long orderItemId;

    @Embedded
    @Column(nullable = false)
    private ExpirationPeriod expirationPeriod;

    private CouponCard(Coupon coupon, Member member){
        this.coupon = coupon;
        this.member = member;
        this.expirationPeriod = coupon.getExpirationPeriod().CopyExpirationPeriod(); //시작날짜 마감날짜만 가져옴
        this.couponCode = UUID.randomUUID();

        if(LocalDateTime.now().isBefore(this.expirationPeriod.getStartDate()))
            this.status = CouponStatus.NOT_YET_ACTIVE;
        else
            this.status = CouponStatus.AVAILABLE;
    }

    public static CouponCard of(Coupon coupon, Member member){
        return new CouponCard(coupon, member);
    }
    public static CouponCard of(Coupon coupon){
        return new CouponCard(coupon, null);
    }
    public void connectMember(Member member){
        this.member = member;
    }

    public boolean validate(OrderItem orderItem) {
        return validateExpirationPeriod() && validateStatus()
                && validateMinimumPurchaseAmount(orderItem.getOrderPrice());
    }
    private boolean validateMinimumPurchaseAmount(int price){
        return this.coupon.validateMinimumPurchaseAmount(price);
    }

    private boolean validateExpirationPeriod(){
        return LocalDateTime.now().isAfter(getExpirationPeriod().getStartDate())
                && LocalDateTime.now().isBefore(getExpirationPeriod().getEndDate());
    }

    private boolean validateStatus(){
        return getStatus().getValue().equals(CouponStatus.AVAILABLE.getValue());
    }

    public void use(OrderItem orderItem) {
        this.status = CouponStatus.USED;
        this.usedDate = LocalDateTime.now();

        orderItem.applyCouponCard(this);
        orderItem.discount(orderItem.getOrderPrice() * getCoupon().getDiscountPercent() / 100);
    }

    public void cancel(OrderItem orderItem){
        this.status = CouponStatus.AVAILABLE;
        this.usedDate = null;
        orderItem.cancelCouponCard();
    }

}
