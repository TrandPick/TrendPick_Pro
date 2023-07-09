package project.trendpick_pro.domain.withdraw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.service.WithdrawService;
import project.trendpick_pro.global.rsData.RsData;

import java.util.List;

@Controller
@RequestMapping("/trendpick/admin")
@RequiredArgsConstructor
public class AdmWithdrawController {
    private final WithdrawService withdrawService;
    private final Rq rq;

    @PreAuthorize("hasAuthority({'ADMIN', 'BRAND_ADMIN'})")
    @GetMapping("/withDrawList")
    public String showApplyList(Model model) {
        Member member=rq.getRollMember();
        List<WithdrawApply> withdrawApplies;
        if(member.getRole().getValue().equals("ADMIN")) {
            withdrawApplies = withdrawService.findAll();
        }else{
            withdrawApplies=withdrawService.findByWithdrawApplyId(member.getId());
        }
        model.addAttribute("withdrawApplies", withdrawApplies);
        return "trendpick/admin/withDrawList";
    }

    @PreAuthorize("hasAuthority({'ADMIN'})")
    @PostMapping("/{withdrawApplyId}")
    public String applyDone(@PathVariable Long withdrawApplyId) {
        RsData withdrawRsData = withdrawService.withdraw(withdrawApplyId);

        return rq.redirectWithMsg("/trendpick/admin/withDrawList", withdrawRsData);
    }

    @PreAuthorize("hasAuthority({'ADMIN'})")
    @PostMapping("/{withdrawApplyId}/cancel")
    public String cancel(@PathVariable Long withdrawApplyId) {
        RsData withdrawRsData = withdrawService.cancelApply(withdrawApplyId);

        return rq.redirectWithMsg("/trendpick/admin/withDrawList", withdrawRsData);
    }
}