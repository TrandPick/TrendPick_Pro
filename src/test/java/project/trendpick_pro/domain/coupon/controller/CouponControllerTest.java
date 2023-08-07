package project.trendpick_pro.domain.coupon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import project.trendpick_pro.ControllerTestSupport;
import project.trendpick_pro.domain.coupon.entity.dto.request.StoreCouponSaveRequest;
import project.trendpick_pro.domain.coupon.entity.dto.response.CouponResponse;
import project.trendpick_pro.global.util.rsData.RsData;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest extends ControllerTestSupport {

    @WithMockUser(username = "testUser", roles = {"BRAND_ADMIN"})
    @DisplayName("쿠폰을 발행한다.")
    @Test
    void createCoupon() throws Exception {
        //given
        StoreCouponSaveRequest request = StoreCouponSaveRequest.builder()
                .name("coupon")
                .limitCount(100)
                .limitIssueDate(10)
                .minimumPurchaseAmount(5000)
                .discountPercent(50)
                .expirationType("ISSUE_AFTER_DATE")
                .issueAfterDate(21)
                .build();
        given(couponService.createCoupon(anyString(), any(StoreCouponSaveRequest.class))).willReturn(RsData.successOf("쿠폰이 발행되었습니다."));

        //when //then
        // params StoreCouponSaveRequest
        mockMvc.perform(post("/trendpick/coupons/polo/generate")
                .param("name", request.getName())
                .param("limitCount", String.valueOf(request.getLimitCount()))
                .param("limitIssueDate", String.valueOf(request.getLimitIssueDate()))
                .param("minimumPurchaseAmount", String.valueOf(request.getMinimumPurchaseAmount()))
                .param("discountPercent", String.valueOf(request.getDiscountPercent()))
                .param("expirationType", request.getExpirationType())
                .param("issueAfterDate", String.valueOf(request.getIssueAfterDate())))
                .andExpect(status().isOk());
    }

    @DisplayName("발행했던 쿠폰들을 확인한다.")
    @Test
    void showAllCoupons() throws Exception {
        //given
        List<CouponResponse> couponResponses = new ArrayList<>();
        couponResponses.add(new CouponResponse());
        couponResponses.add(new CouponResponse());
        given(couponService.findAllCoupons()).willReturn(couponResponses);

        //when //then
        mockMvc.perform(get("/trendpick/coupons/list"))
                .andExpect(status().isOk());
    }

    @DisplayName("특정 상품에 대한 쿠폰들만 조회한다.")
    @Test
    void showCouponsByProduct() throws Exception {
        //given
        given(couponService.findCouponsByProduct(anyLong())).willReturn(new ArrayList<>());

        //when //then
        mockMvc.perform(get("/trendpick/coupons/box")
                        .param("productId", "1")
        )
        .andDo(print())
        .andExpect(status().isOk());

    }
}