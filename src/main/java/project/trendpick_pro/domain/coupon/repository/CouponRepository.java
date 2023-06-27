package project.trendpick_pro.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.trendpick_pro.domain.coupon.entity.Coupon;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("select c from Coupon c where c.store.brand = :brandName")
    List<Coupon> findAllByBrand(@Param("brandName") String brandName);
}
