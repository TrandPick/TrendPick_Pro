package project.trendpick_pro.domain.coupon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.coupon.entity.Coupon;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponCardByApplyResponse;
import project.trendpick_pro.domain.coupon.exception.CouponNotFoundException;
import project.trendpick_pro.domain.coupon.repository.CouponCardRepository;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.coupon.service.CouponCardService;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.exceoption.OrderItemNotFoundException;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponCardServiceImpl implements CouponCardService {

    private final CouponCardRepository couponCardRepository;
    private final CouponRepository couponRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public RsData<CouponCard> issue(Member member, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
        int count = couponCardRepository.countByCouponIdAndMemberId(couponId, member.getId());
        if(count > 0)
            return RsData.of("F-3", "이미 발급 받으신 쿠폰입니다.");
        if(!coupon.validateLimitCount())
            return RsData.of("F-1", "수량이 모두 소진되었습니다.");
        if(!coupon.validateLimitIssueDate())
            return RsData.of("F-2", "쿠폰 발급 가능 날짜가 지났습니다.");

        CouponCard savedCouponCard = couponCardRepository.save(CouponCard.of(coupon, member));
        return RsData.of("S-1", coupon.getName() + " 쿠폰이 발급되었습니다.", savedCouponCard);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CouponCardByApplyResponse> showCouponCardsByOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
        List<CouponCard> couponCards = couponCardRepository.findAllByBrand(orderItem.getProduct().getBrand().getName());
        return createCouponCardByApplyResponseList(couponCards, orderItem);
    }

    @Transactional
    @Override
    public RsData apply(Long couponCardId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
        CouponCard couponCard = couponCardRepository.findById(couponCardId).orElseThrow(
                () -> new CouponNotFoundException("존재하지 않은 쿠폰입니다."));
        if (!couponCard.validate(orderItem))
            return RsData.of("F-1", "해당 주문상품에 적용된 쿠폰이 없습니다.");
        couponCard.use(orderItem);
        return RsData.of("S-1", "쿠폰이 적용되었습니다.");
    }

    @Transactional
    @Override
    public RsData cancel(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
        CouponCard couponCard = orderItem.getCouponCard();
        couponCard.cancel(orderItem);
        return RsData.of("S-1", "쿠폰이 취소되었습니다.");
    }

    private List<CouponCardByApplyResponse> createCouponCardByApplyResponseList(List<CouponCard> couponCards, OrderItem orderItem) {
        return couponCards.stream()
                .filter(couponCard -> couponCard.validate(orderItem))
                .map(couponCard -> CouponCardByApplyResponse.of(couponCard, orderItem))
                .toList();
    }
}

