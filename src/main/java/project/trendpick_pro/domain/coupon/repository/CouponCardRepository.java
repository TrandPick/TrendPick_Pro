package project.trendpick_pro.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.coupon.entity.CouponCard;

public interface CouponCardRepository extends JpaRepository<CouponCard, Long> {
    @Query("select count(cc) from CouponCard cc" +
            " where cc.coupon.id = :couponId and cc.member.id = :memberId")
    int countByCouponIdAndMemberId(@Param("couponId") Long couponId, @Param("memberId") Long memberId);
}
