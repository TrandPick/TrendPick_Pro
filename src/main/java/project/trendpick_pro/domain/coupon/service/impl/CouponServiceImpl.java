package project.trendpick_pro.domain.coupon.service.impl;

import project.trendpick_pro.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.dto.response.SimpleCouponResponse;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationType;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final StoreService storeService;
    private final ProductService productService;

    @Override
    @Transactional
    public RsData<String> generate(String storeName, StoreCouponSaveRequest storeCouponSaveRequest) {
        RsData<String> validateExpirationPeriodResult = validateExpirationPeriod(storeCouponSaveRequest);
        if (validateExpirationPeriodResult.isFail())
            return validateExpirationPeriodResult;

        Coupon coupon = Coupon.generate(storeService.findByBrand(storeName), storeCouponSaveRequest);
        couponRepository.save(coupon);
        return RsData.of("S-1", "쿠폰이 성공적으로 발급되었습니다.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleCouponResponse> findAllCoupons() {
        return convertCouponsToResponses(couponRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public  List<SimpleCouponResponse> findCouponsByProduct(Long productId) {
        Product product = productService.findByIdWithBrand(productId);
        List<Coupon> coupons = filteredCoupons(couponRepository.findAllByBrand(product.getBrand().getName()), product);
        return convertCouponsToResponses(coupons);
    }

    private List<SimpleCouponResponse> convertCouponsToResponses(List<Coupon> coupons) {
        return coupons.stream()
                .map(SimpleCouponResponse::of)
                .toList();
    }

    private List<Coupon> filteredCoupons(List<Coupon> coupons, Product product) {
        return coupons.stream()
                .filter(coupon -> coupon.validateMinimumPurchaseAmount(product.getProductOption().getPrice())
                        && coupon.validateLimitCount()
                        && coupon.validateLimitIssueDate())
                .toList();
    }

    private static RsData<String> validateExpirationPeriod(StoreCouponSaveRequest storeCouponSaveRequest) {
        if (storeCouponSaveRequest.getExpirationType().equals(ExpirationType.ISSUE_AFTER_DATE.getValue())) {
            if (storeCouponSaveRequest.getIssueAfterDate() == null)
                return RsData.of("F-4", "발급된 이후의 유효기간을 설정하셔야 합니다.");
            return RsData.of("S-2", "유효기간 검증 성공");
        }

        if (storeCouponSaveRequest.getStartDate() == null || storeCouponSaveRequest.getEndDate() == null)
            return RsData.of("F-3", "날짜 직접선택 타입은 시작날짜와 마감날짜를 모두 선택하셔야 합니다.");
        if (storeCouponSaveRequest.getStartDate().isBefore(LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)))
            return RsData.of("F-1", "쿠폰 발급 시작일은 다음날 00시 00분부터 가능합니다.");
        if (storeCouponSaveRequest.getEndDate().isBefore(storeCouponSaveRequest.getStartDate().plusDays(1)))
            return RsData.of("F-2", "마감 기한은 발급 날짜보다 하루 이상 이후여야 합니다.");

        return RsData.of("S-1", "검증성공");
    }
}