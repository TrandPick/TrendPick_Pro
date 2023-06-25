package project.trendpick_pro.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.coupon.entity.CouponCard;

public interface CouponCardRepository extends JpaRepository<CouponCard, Long> {
}
