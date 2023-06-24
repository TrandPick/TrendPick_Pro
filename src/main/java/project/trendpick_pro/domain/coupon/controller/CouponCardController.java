package project.trendpick_pro.domain.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.coupon.service.CouponCardService;
import project.trendpick_pro.global.rsData.RsData;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/couponCards")
public class CouponCardController {
    private final Rq rq;
    private final CouponCardService couponCardService;
    @PostMapping("/{couponId}/issue")
    public String issue(@PathVariable("couponId") Long couponId){
        RsData<CouponCard> result = couponCardService.issue(rq.getMember(), couponId);
        if(result.isFail())
            return rq.historyBack(result);
        return rq.redirectWithMsg("/trendpick/coupons/list", result);
    }
}
