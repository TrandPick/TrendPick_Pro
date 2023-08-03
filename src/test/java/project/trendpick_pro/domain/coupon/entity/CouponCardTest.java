package project.trendpick_pro.domain.coupon.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CouponCardTest {

    @DisplayName("생성되어진 쿠폰을 사용자가 신청하면 쿠폰카드를 생성한다. 이때 쿠폰의 번호는 UUID로 생성된다.")
    @Test
    void createCouponCard() throws Exception {
        //given
        Coupon coupon = createCoupon();

        //when
        CouponCard couponCard1 = new CouponCard(coupon);
        CouponCard couponCard2 = new CouponCard(coupon);

        //then
        assertThat(couponCard1.getCouponCode()).isNotEqualTo(couponCard2.getCouponCode());
    }

    @DisplayName("생성된 쿠폰카드는 쿠폰의 ISSUE_AFTER_DATE 만료기간을 따른다.")
    @Test
    void useCouponCardPERIOD() throws Exception {
        //given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 8, 1, 10, 10, 10);
        Coupon coupon = createCoupon();
        coupon.assignPostIssueExpiration(10);

        //when
        CouponCard couponCard = new CouponCard(coupon);
        couponCard.updatePeriod(registeredDateTime);

        //then
        assertThat(couponCard.getExpirationPeriod()).isEqualTo(coupon.getExpirationPeriod());
        assertThat(couponCard.getExpirationPeriod().getExpirationType()).isEqualTo(ExpirationType.ISSUE_AFTER_DATE);
        assertThat(couponCard.getStatus()).isEqualTo(CouponStatus.AVAILABLE);
    }

    @DisplayName("생성된 쿠폰카드는 쿠폰의 PERIOD 만료기간을 따른다.")
    @Test
    void useCouponCardISSUE_AFTER_DATE() throws Exception {
        //given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 8, 1, 10, 10, 10);
        Coupon coupon = createCoupon();
        coupon.assignPeriodExpiration(registeredDateTime, registeredDateTime.plusDays(10));

        //when
        CouponCard couponCard = new CouponCard(coupon);
        couponCard.updatePeriod(registeredDateTime);

        //then
        assertThat(couponCard.getExpirationPeriod()).isEqualTo(coupon.getExpirationPeriod());
        assertThat(couponCard.getExpirationPeriod().getExpirationType()).isEqualTo(ExpirationType.PERIOD);
        assertThat(couponCard.getStatus()).isEqualTo(CouponStatus.AVAILABLE);
    }

    private static Coupon createCoupon() {
        return Coupon.builder()
                .name("couponName")
                .limitCount(100)
                .limitIssueDate(10)
                .minimumPurchaseAmount(1000)
                .discountPercent(10)
                .build();
    }
}