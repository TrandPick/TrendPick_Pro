package project.trendpick_pro.domain.coupon.service;

import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.dto.response.SimpleCouponResponse;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

public interface CouponService {
    RsData<String> generate(String storeName, StoreCouponSaveRequest storeCouponSaveRequest);
    List<SimpleCouponResponse> findAllCoupons();
    List<SimpleCouponResponse> findCouponsByProduct(Long productId);
}
