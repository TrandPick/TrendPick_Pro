package project.trendpick_pro.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.store.entity.Store;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
