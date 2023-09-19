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
import project.trendpick_pro.domain.orders.exception.OrderItemNotFoundException;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponCardServiceImpl implements CouponCardService {
    private final CouponCardRepository couponCardRepository;
    private final CouponRepository couponRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public RsData issue(Member member, Long couponId, LocalDateTime dateTime) {
        Coupon coupon = findCoupon(couponId);
        int count = couponCardRepository.countByCouponIdAndMemberId(couponId, member.getId());

        RsData<CouponCard> validateResult = validateCouponCard(count, coupon, dateTime);
        if (validateResult.isFail())
            return validateResult;

        settingCouponCard(member, dateTime, coupon);
        return RsData.of("S-1", coupon.getName() + " 쿠폰이 발급되었습니다.");
    }


    @Transactional
    @Override
    public RsData apply(Long couponCardId, Long orderItemId, LocalDateTime dateTime) {
        OrderItem orderItem = findOrderItem(orderItemId);
        CouponCard couponCard = findCouponCard(couponCardId);

        if (!couponCard.validate(orderItem, dateTime))
            return RsData.of("F-1", "해당 주문상품에 해당 쿠폰을 적용할 수 없습니다.");
        orderItem.applyCouponCard(couponCard);
        return RsData.of("S-1", "쿠폰이 적용되었습니다.");
    }

    private CouponCard findCouponCard(Long couponCardId) {
        CouponCard couponCard = couponCardRepository.findById(couponCardId).orElseThrow(
                () -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
        return couponCard;
    }

    @Transactional
    @Override
    public RsData cancel(Long orderItemId) {
        OrderItem orderItem = findOrderItem(orderItemId);
        orderItem.getCouponCard().cancel(orderItem);
        return RsData.of("S-1", "쿠폰이 취소되었습니다.");
    }

    @Override
    public List<CouponCardByApplyResponse> showCouponCardsByOrderItem(Long orderItemId) {
        OrderItem orderItem = findOrderItem(orderItemId);
        List<CouponCard> couponCards = couponCardRepository.findAllByBrand(orderItem.getProduct().getProductOption().getBrand().getName());
        return createCouponCardByApplyResponseList(couponCards, orderItem);
    }

    private CouponCard settingCouponCard(Member member, LocalDateTime dateTime, Coupon coupon) {
        CouponCard couponCard = new CouponCard(coupon);
        couponCard.updatePeriod(dateTime);
        couponCard.connectMember(member);
        CouponCard savedCouponCard = couponCardRepository.save(couponCard);
        return savedCouponCard;
    }

    private  RsData<CouponCard> validateCouponCard(int count, Coupon coupon, LocalDateTime dateTime) {
        if(count > 0)
            return RsData.of("F-3", "이미 발급 받으신 쿠폰입니다.");
        if(!coupon.validateLimitCount())
            return RsData.of("F-1", "수량이 모두 소진되었습니다.");
        if(!coupon.validateLimitIssueDate(dateTime))
            return RsData.of("F-2", "쿠폰 발급 가능 날짜가 지났습니다.");
        return RsData.success();
    }

    private List<CouponCardByApplyResponse> createCouponCardByApplyResponseList(List<CouponCard> couponCards, OrderItem orderItem) {
        return couponCards.stream()
                .filter(couponCard -> couponCard.validate(orderItem, LocalDateTime.now()))
                .map(couponCard -> CouponCardByApplyResponse.of(couponCard, orderItem))
                .toList();
    }

    private OrderItem findOrderItem(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
    }

    private Coupon findCoupon(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(
                () -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
    }
}

