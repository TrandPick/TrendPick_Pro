package project.trendpick_pro.domain.coupon.service;

import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponResponse;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

public interface CouponService {
    RsData<String> createCoupon(String storeName, StoreCouponSaveRequest storeCouponSaveRequest);
    List<CouponResponse> findAllCoupons();
    List<CouponResponse> findCouponsByProduct(Long productId);
}
