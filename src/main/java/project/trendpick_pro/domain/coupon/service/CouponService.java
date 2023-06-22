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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {
    private final CouponRepository couponRepository;
    private final StoreService storeService;

    @Transactional
    public RsData<Long> issue(Long storeId, StoreCouponSaveRequest storeCouponSaveRequest) {
        RsData<Long> validateResult = validateExpirationPeriod(storeCouponSaveRequest);
        if(validateResult.isFail())
            return validateResult;

        Coupon coupon =
                Coupon.issue(storeService.findById(storeId), storeCouponSaveRequest);
        Coupon savedCoupon = couponRepository.save(coupon);
        return RsData.of("S-1", "쿠폰이 성공적으로 발급되었습니다.", savedCoupon.getId());
    }

    private static RsData<Long> validateExpirationPeriod(StoreCouponSaveRequest storeCouponSaveRequest) {
        if(storeCouponSaveRequest.getStartDate().isBefore(LocalDateTime.now()))
            return RsData.of("F-1", "현재보다 이전 날짜를 시작 날짜로 할 수 없습니다.");
        if(storeCouponSaveRequest.getEndDate().isBefore(LocalDateTime.now().plusDays(1)))
            return RsData.of("F-2", "마감 날짜는 적어도 오늘보다 늦은 기한이어야 합니다.");
        return RsData.of("S-1", "검증성공");
    }
}
