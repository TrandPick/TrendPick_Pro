package project.trendpick_pro.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.trendpick_pro.domain.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
