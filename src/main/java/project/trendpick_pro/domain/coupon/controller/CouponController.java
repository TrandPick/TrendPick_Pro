package project.trendpick_pro.domain.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.coupon.entity.dto.CouponDto;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.repository.CouponRepository;
import project.trendpick_pro.domain.coupon.service.CouponService;
import project.trendpick_pro.global.rsData.RsData;

@Controller
@RequestMapping("/trendpick/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final Rq rq;

    @PostMapping("/{storeId}/register")
    public String issue(@RequestParam("storeId") Long storeId, StoreCouponSaveRequest storeCouponSaveRequest){
        RsData<Long> result = couponService.issue(storeId, storeCouponSaveRequest);
        if (result.isFail())
            rq.historyBack(result);
        return "trendpick/index";
    }


}
