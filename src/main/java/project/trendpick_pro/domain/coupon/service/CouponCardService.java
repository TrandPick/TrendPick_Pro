package project.trendpick_pro.domain.coupon.service;

import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponCardByApplyResponse;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.global.util.rsData.RsData;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponCardService {

    RsData issue(Member member, Long couponId, LocalDateTime dateTime);

    List<CouponCardByApplyResponse> showCouponCardsByOrderItem(Long orderItemId);

    RsData apply(Long couponCardId, Long orderItemId, LocalDateTime now);

    RsData cancel(Long orderItemId);

}
