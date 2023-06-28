package project.trendpick_pro.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponCardByApplyResponse;
import project.trendpick_pro.domain.coupon.repository.CouponCardRepository;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.global.rsData.RsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponCardService {
    private final CouponCardRepository couponCardRepository;
    private final CouponRepository couponRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
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

    public List<CouponCardByApplyResponse> showCouponCardsByOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new IllegalArgumentException("해당 주문상품은 존재하지 않습니다.")
        );

        List<CouponCard> couponCardList = couponCardRepository.findAllByBrand(orderItem.getProduct().getBrand().getName());
        List<CouponCardByApplyResponse> responses = new ArrayList<>();
        for (CouponCard couponCard : couponCardList) {
            if (couponCard.validate(orderItem))
                responses.add(CouponCardByApplyResponse.of(couponCard, orderItem));
        }
        return responses;
    }

    @Transactional
    public RsData apply(Long couponCardId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new IllegalArgumentException("해당 주문상품은 존재하지 않습니다.")
        );

        CouponCard couponCard = couponCardRepository.findById(couponCardId).orElse(null);
        if(couponCard == null)
            return RsData.of("F-1", "존재하지 않는 쿠폰입니다.");
        if (!couponCard.validate(orderItem))
            return RsData.of("F-2", "해당 주문상품에는 사용하실 수 없는 쿠폰입니다.");

        couponCard.use(orderItem);
        return RsData.of("S-1", "쿠폰이 적용되었습니다.");
    }

    @Transactional
    public RsData cancel(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new IllegalArgumentException("해당 주문상품은 존재하지 않습니다.")
        );
        CouponCard couponCard = orderItem.getCouponCard();
        if(couponCard == null)
            return RsData.of("F-1", "해당 주문상품에 적용된 쿠폰이 없습니다.");

        couponCard.cancel(orderItem);
        return RsData.of("S-1", "쿠폰이 취소되었습니다.");
    }
}
