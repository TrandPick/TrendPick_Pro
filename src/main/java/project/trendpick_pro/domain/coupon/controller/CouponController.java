package project.trendpick_pro.domain.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.service.CouponService;
import project.trendpick_pro.global.rsData.RsData;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/trendpick/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final Rq rq;

    @GetMapping("/{storeName}/generate")
    public String generate(@PathVariable("storeName") String storeName, StoreCouponSaveRequest storeCouponSaveRequest, Model model) {
        model.addAttribute("couponForm", storeCouponSaveRequest);
        model.addAttribute("storeName", storeName);
        return "/trendpick/store/coupon/generate";
    }

    @PostMapping("/{storeName}/generate")
    public String generate(@PathVariable("storeName") String storeName, @Valid StoreCouponSaveRequest storeCouponSaveRequest) throws UnsupportedEncodingException {
        RsData<String> result = couponService.generate(storeName, storeCouponSaveRequest);
        if (result.isFail())
            return rq.historyBack(result);
        return rq.redirectWithMsg("/trendpick/coupons/%s/list".formatted(storeName), "쿠폰이 성공적으로 발급되었습니다.");
    }

    @GetMapping("/{storeName}/list")
    public String list(@PathVariable("storeName") String storeName, Model model){

        return "trendpick/store/coupon/list";
    }
}
