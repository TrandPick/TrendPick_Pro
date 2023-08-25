package project.trendpick_pro.domain.rebate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.rebate.entity.MonthRebateData;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.service.RebateService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.util.Ut;
import java.util.List;

@Controller
@RequestMapping("/trendpick/admin")
@RequiredArgsConstructor
public class AdmRebateController {
    private final RebateService rebateService;
    private final Rq rq;

    @GetMapping("/makeData")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})") //브랜드 관리자만 접근 가능
    public String showMakeData() {
        return "trendpick/admin/makeData";
    }

    @PostMapping("/makeData")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    public String makeData(String yearMonth) {
        RsData makeDateRsData = rebateService.makeRebateOrderItem(rq.getBrandName(), yearMonth);
        if (makeDateRsData.isFail()) {
            return rq.historyBack("정산할 데이터가 없습니다.");
        }
        return rq.redirectWithMsg("/trendpick/admin/rebateOrderItemList?yearMonth=" + yearMonth, makeDateRsData);
    }

    @GetMapping("/rebateOrderItemList")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    public String showRebateOrderItemList(String yearMonth, Model model) {
        if (!StringUtils.hasText(yearMonth))
            yearMonth = Ut.date.getCurrentYearMonth();

        List<RebateOrderItem> items = rebateService.findRebateOrderItemByCurrentYearMonth(rq.getBrandName(), yearMonth);
        MonthRebateData monthRebateData = rebateService.findMonthRebateData(rq.getBrandName(), yearMonth);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("items", items);
        model.addAttribute("monthRebateData", monthRebateData);

        return "trendpick/admin/rebateOrderItemList";
    }

    @PostMapping("/rebateOne/{orderItemId}")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    public String rebateOne(@PathVariable long orderItemId, HttpServletRequest req) {
        RsData result = rebateService.rebate(rq.getBrandName(), orderItemId);
        if (result.isFail()) {
            return rq.historyBack(result);
        }
        String yearMonth = Ut.url.getQueryParamValue(req.getHeader("Referer"), "yearMonth", "");
        return rq.redirectWithMsg("/trendpick/admin/rebateOrderItemList?yearMonth=" + yearMonth, result);
    }

    @PostMapping("/rebate")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    public String rebate(@RequestParam String yearMonth) { //전체 정산처리
        RsData result = rebateService.rebate(rq.getBrandName(), yearMonth);
        if(result.isFail())
            return rq.historyBack(result);
        return rq.redirectWithMsg("/trendpick/admin/rebateOrderItemList?yearMonth=" + yearMonth, result);
    }
}
