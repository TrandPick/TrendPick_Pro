package project.trendpick_pro.domain.rebate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.service.RebateService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;
import project.trendpick_pro.global.util.Ut;

import java.util.Arrays;
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
        RsData makeDateRsData = rebateService.makeData(rq.getBrandName(), yearMonth);
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

        List<RebateOrderItem> items = rebateService.findRebateDataByCurrentYearMonth(rq.getBrandName(), yearMonth);

        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("items", items);
        return "trendpick/admin/rebateOrderItemList";
    }

    @PostMapping("/rebateOne/{orderItemId}")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    public String rebateOne(@PathVariable long orderItemId, HttpServletRequest req) {
        RsData result = rebateService.rebate(orderItemId);
        if (result.isFail()) {
            return rq.historyBack(result);
        }
        String yearMonth = Ut.url.getQueryParamValue(req.getHeader("Referer"), "yearMonth", "");
        return rq.redirectWithMsg("/trendpick/admin/rebateOrderItemList?yearMonth=" + yearMonth, result);
    }

    @PostMapping("/rebate")
    @PreAuthorize("hasAuthority({'BRAND_ADMIN'})")
    public String rebate(String ids, HttpServletRequest req) {
        String[] idsArr = ids.split(",");
        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(rebateService::rebate);
        String yearMonth = Ut.url.getQueryParamValue(req.getHeader("Referer"), "yearMonth", "");

        String redirect = "redirect:/trendpick/admin/rebateOrderItemList?yearMonth=" + yearMonth;
        redirect += "&msg=" + Ut.url.encode("%d건의 정산품목을 정산처리하였습니다.".formatted(idsArr.length));
        return redirect;
    }
}
