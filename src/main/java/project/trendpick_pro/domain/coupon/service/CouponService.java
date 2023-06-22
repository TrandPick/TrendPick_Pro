package project.trendpick_pro.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.dto.CouponDto;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.service.StoreService;
import project.trendpick_pro.global.rsData.RsData;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {
    private final CouponRepository couponRepository;
    private final StoreService storeService;

    @Transactional
    public RsData<String> issue(String storeName, StoreCouponSaveRequest storeCouponSaveRequest) {
        RsData<String> validateResult = validateExpirationPeriod(storeCouponSaveRequest);
        if(validateResult.isFail())
            return validateResult;

        Coupon coupon =
                Coupon.issue(storeService.findByBrand(storeName), storeCouponSaveRequest);
        Coupon savedCoupon = couponRepository.save(coupon);
        return RsData.of("S-1", "쿠폰이 성공적으로 발급되었습니다.");
    }

    private static RsData<String> validateExpirationPeriod(StoreCouponSaveRequest storeCouponSaveRequest) {
        LocalDateTime nextDayMidnight = LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT);

        if (storeCouponSaveRequest.getStartDate().isBefore(nextDayMidnight)) {
            return RsData.of("F-1", "쿠폰 발급 시작일은 다음날 00시 00분부터 가능합니다.");
        }

        if (storeCouponSaveRequest.getEndDate().isBefore(storeCouponSaveRequest.getStartDate().plusDays(1))) {
            return RsData.of("F-2", "마감 기한은 발급 날짜보다 하루 이상 이후여야 합니다.");
        }

        return RsData.of("S-1", "검증 성공");
    }
}
