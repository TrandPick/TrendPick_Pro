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
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
        int count = couponCardRepository.countByCouponIdAndMemberId(couponId, member.getId());

        RsData<CouponCard> validateResult = validateCouponCard(count, coupon);
        if (validateResult != null)
            return validateResult;

        CouponCard savedCouponCard = settingCouponCard(member, dateTime, coupon);
        return RsData.of("S-1", coupon.getName() + " 쿠폰이 발급되었습니다.");
    }

    @Transactional
    @Override
    public RsData apply(Long couponCardId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
        CouponCard couponCard = couponCardRepository.findById(couponCardId).orElseThrow(
                () -> new CouponNotFoundException("존재하지 않는 쿠폰입니다."));
        if (!couponCard.validate(orderItem, LocalDateTime.now()))
            return RsData.of("F-1", "해당 주문상품에 해당 쿠폰을 적용할 수 없습니다.");
        couponCard.use(orderItem, LocalDateTime.now());
        return RsData.of("S-1", "쿠폰이 적용되었습니다.");
    }

    @Transactional
    @Override
    public RsData cancel(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
        orderItem.getCouponCard().cancel(orderItem);
        return RsData.of("S-1", "쿠폰이 취소되었습니다.");
    }

    @Override
    public List<CouponCardByApplyResponse> showCouponCardsByOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("주문되지 않은 상품입니다."));
        List<CouponCard> couponCards = couponCardRepository.findAllByBrand(orderItem.getProduct().getProductOption().getBrand().getName());
        return createCouponCardByApplyResponseList(couponCards, orderItem);
    }

    private CouponCard settingCouponCard(Member member, LocalDateTime dateTime, Coupon coupon) {
        CouponCard couponCard = new CouponCard(coupon);
        couponCard.updatePeriod(dateTime);
        CouponCard savedCouponCard = couponCardRepository.save(couponCard);
        savedCouponCard.updatePeriod(dateTime);
        savedCouponCard.connectMember(member);
        return savedCouponCard;
    }

    private static RsData<CouponCard> validateCouponCard(int count, Coupon coupon) {
        if(count > 0)
            return RsData.of("F-3", "이미 발급 받으신 쿠폰입니다.");
        if(!coupon.validateLimitCount())
            return RsData.of("F-1", "수량이 모두 소진되었습니다.");
        if(!coupon.validateLimitIssueDate(LocalDateTime.now()))
            return RsData.of("F-2", "쿠폰 발급 가능 날짜가 지났습니다.");
        return null;
    }

    private List<CouponCardByApplyResponse> createCouponCardByApplyResponseList(List<CouponCard> couponCards, OrderItem orderItem) {
        return couponCards.stream()
                .filter(couponCard -> couponCard.validate(orderItem, LocalDateTime.now()))
                .map(couponCard -> CouponCardByApplyResponse.of(couponCard, orderItem))
                .toList();
    }
}

