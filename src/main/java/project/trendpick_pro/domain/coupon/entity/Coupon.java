package project.trendpick_pro.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.store.entity.Store;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store issuer;

    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "limit_count")
    private int limitCount;
    @Column(name = "issue_count")
    private int issueCount;

    @Column(name = "discount_percent")
    private int discountPercent;
    @Column(name = "coupon_number")
    private String couponNumber;

    @Builder
    private Coupon(Store issuer, String name, int limitCount, int discountPercent, LocalDateTime startDate, LocalDateTime endDate){
        this.issuer = issuer;
        this.name = name;
        this.limitCount = limitCount;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issueCount = 0;
        this.couponNumber = UUID.randomUUID().toString();
    }

//    public static Coupon of(StoreCouponSaveRequest storeCouponSaveRequest){
//        return Coupon
//                .builder()
//                .issuer(storeCouponSaveRequest)
//                .build()
//    }
}
