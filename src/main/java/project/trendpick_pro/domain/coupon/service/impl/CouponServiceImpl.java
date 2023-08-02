package project.trendpick_pro.domain.coupon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponResponse;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationType;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.coupon.service.CouponService;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.product.service.ProductService;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final StoreService storeService;
    private final ProductService productService;

    @Transactional
    @Override
    public RsData<String> createCoupon(String storeName, StoreCouponSaveRequest request) {
        RsData<String> validateExpirationPeriodResult = validateExpirationPeriod(request);
        if (validateExpirationPeriodResult.isFail()) {
            return validateExpirationPeriodResult;
        }
        Store store = storeService.findByBrand(storeName);
        Long couponId = settingCoupon(request, store);
        return RsData.of("S-1", "쿠폰이 성공적으로 발급 되었습니다.", couponId.toString());
    }

    @Override
    public List<CouponResponse> findAllCoupons() {
        return convertCouponsToResponses(couponRepository.findAll());
    }

    @Override
    public List<CouponResponse> findCouponsByProduct(Long productId) {
        Product product = productService.findByIdWithBrand(productId);
        List<Coupon> coupons = filteredCoupons(product);
        return convertCouponsToResponses(coupons);
    }

    private Long settingCoupon(StoreCouponSaveRequest request, Store store) {
        Coupon coupon = Coupon.of(request, store.getBrand());
        updateExpirationType(request, coupon);
        coupon.connectStore(store);
        return couponRepository.save(coupon).getId();
    }

    private static void updateExpirationType(StoreCouponSaveRequest request, Coupon coupon) {
        if(request.getExpirationType().equals(ExpirationType.PERIOD.getValue()))
            coupon.assignPeriodExpiration(request.getStartDate(), request.getEndDate());
        else if(request.getExpirationType().equals(ExpirationType.ISSUE_AFTER_DATE.getValue()))
            coupon.assignPostIssueExpiration(request.getIssueAfterDate());
    }

    private List<CouponResponse> convertCouponsToResponses(List<Coupon> coupons) {
        return coupons.stream()
                .map(CouponResponse::of)
                .toList();
    }

    private List<Coupon> filteredCoupons(Product product) {
        List<Coupon> coupons = couponRepository.findAllByBrand(product.getProductOption().getBrand().getName());
        return coupons.stream()
                .filter(coupon -> coupon.validateMinimumPurchaseAmount(product.getProductOption().getPrice())
                        && coupon.validateLimitCount()
                        && coupon.validateLimitIssueDate(LocalDateTime.now()))
                .toList();
    }

    private static RsData<String> validateExpirationPeriod(StoreCouponSaveRequest request) {
        if (request.getExpirationType().equals(ExpirationType.ISSUE_AFTER_DATE.getValue())) {
            if (request.getIssueAfterDate() == null)
                return RsData.of("F-4", "발급된 이후의 유효기간을 설정하셔야 합니다.");
            return RsData.of("S-2", "유효기간 검증 성공");
        }

        if (request.getStartDate() == null || request.getEndDate() == null)
            return RsData.of("F-3", "날짜 직접선택 타입은 시작날짜와 마감날짜를 모두 선택하셔야 합니다.");
        if (request.getStartDate().isBefore(LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)))
            return RsData.of("F-1", "쿠폰 발급 시작일은 다음날 00시 00분부터 가능합니다.");
        if (request.getEndDate().isBefore(request.getStartDate().plusDays(1)))
            return RsData.of("F-2", "마감 기한은 발급 날짜보다 하루 이상 이후여야 합니다.");

        return RsData.of("S-1", "검증성공");
    }
}