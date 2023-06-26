package project.trendpick_pro.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.coupon.repository.CouponCardRepository;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.global.rsData.RsData;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponCardService {
    private final CouponCardRepository couponCardRepository;
    private final CouponRepository couponRepository;

    public RsData<CouponCard> issue(Member member, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 쿠폰입니다.")
        );

        int count = couponCardRepository.countByCouponIdAndMemberId(couponId, member.getId());
        if(count > 0)
            return RsData.of("F-3", "이미 발급 받으신 쿠폰입니다.");

        if(!coupon.validateLimitCount())
            return RsData.of("F-1", "수량이 모두 소진되었습니다.");
        if(!coupon.validateLimitIssueDate())
            return RsData.of("F-2", "쿠폰 발급 가능 날짜가 지났습니다.");

        CouponCard savedCouponCard = couponCardRepository.save(CouponCard.of(coupon, member));
        return RsData.of("S-1", "%s 쿠폰이 발급되었습니다.".formatted(coupon.getName()), savedCouponCard);
    }
}
