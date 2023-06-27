package project.trendpick_pro.domain.coupon.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.coupon.entity.CouponCard;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponCardByApplyResponse;
import project.trendpick_pro.domain.coupon.service.CouponCardService;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/couponCards")
public class CouponCardController {
    private final Rq rq;
    private final CouponCardService couponCardService;

    @PostMapping("/{couponId}/issue")
    public String issue(@PathVariable("couponId") Long couponId, HttpServletRequest req) {
        RsData<CouponCard> result = couponCardService.issue(rq.getMember(), couponId);
        String referer = req.getHeader("referer");
        if (result.isFail())
            return rq.historyBack(result);
        return rq.redirectWithMsg(referer, result);
    }

    @GetMapping("apply")
    @ResponseBody
    public List<CouponCardByApplyResponse> apply(@RequestParam("orderItem") Long orderItemId, Model model) {
//        model.addAttribute("couponCards", couponCardService.showCouponCardsByOrderItem(orderItemId));
//        model.addAttribute("orderItemId", orderItemId);
//        return "trendpick/coupons/couponcards";
        return couponCardService.showCouponCardsByOrderItem(orderItemId);
    }

    @PostMapping("apply")
    public String apply(@RequestParam("couponCard") Long couponCardId, @RequestParam("orderItem") Long orderItemId, HttpServletRequest req) {
        RsData result = couponCardService.apply(couponCardId, orderItemId);
        if (result.isFail())
            return rq.historyBack(result);

        String referer = req.getHeader("referer");
        return rq.redirectWithMsg(referer, "쿠폰이 적용되었습니다.");
    }

    @PostMapping("cancel")
    public String cancel(@RequestParam("orderItem") Long orderItemId, HttpServletRequest req) {
        RsData result = couponCardService.cancel(orderItemId);
        if(result.isFail())
            return rq.historyBack(result);
        String referer = req.getHeader("referer");
        return rq.redirectWithMsg(referer, result);
    }
}
