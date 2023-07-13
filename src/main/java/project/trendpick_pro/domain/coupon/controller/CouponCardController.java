package project.trendpick_pro.domain.coupon.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponCardByApplyResponse;
import project.trendpick_pro.domain.coupon.service.CouponCardService;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/usr/couponCards")
public class CouponCardController {

    private final Rq rq;
    private final CouponCardService couponCardService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/{couponId}/issue")
    public String issueCoupon(@PathVariable("couponId") Long couponId, HttpServletRequest req) {
        return processRequest(couponCardService.issue(rq.getMember(), couponId), "쿠폰이 발급되었습니다.", req);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("apply")
    @ResponseBody
    public List<CouponCardByApplyResponse> showApplicableCoupons(@RequestParam("orderItem") Long orderItemId) {
        return couponCardService.showCouponCardsByOrderItem(orderItemId);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("apply")
    public String applyCoupon(@RequestParam("couponCard") Long couponCardId, @RequestParam("orderItem") Long orderItemId, HttpServletRequest req) {
        return processRequest(couponCardService.apply(couponCardId, orderItemId), "쿠폰이 적용되었습니다.", req);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("cancel")
    public String cancelCoupon(@RequestParam("orderItem") Long orderItemId, HttpServletRequest req) {
        return processRequest(couponCardService.cancel(orderItemId), "쿠폰 적용이 취소되었습니다.", req);
    }

    private String processRequest(RsData result, String successMsg, HttpServletRequest req) {
        if(result.isFail()) {
            return rq.historyBack(result);
        }
        String referer = req.getHeader("referer");
        return rq.redirectWithMsg(referer, successMsg);
    }
}
