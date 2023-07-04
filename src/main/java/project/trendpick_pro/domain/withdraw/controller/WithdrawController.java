package project.trendpick_pro.domain.withdraw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.withdraw.entity.WithdrawApply;
import project.trendpick_pro.domain.withdraw.entity.dto.WithDrawApplyForm;
import project.trendpick_pro.domain.withdraw.service.WithdrawService;
import project.trendpick_pro.global.rsData.RsData;

@Controller
@RequestMapping("/trendpick/admin")
@RequiredArgsConstructor
@Slf4j
public class WithdrawController {
    private final WithdrawService withdrawService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/withDraw")
    public String showApply(Model model) {
        long actorRestCash = memberService.getRestCash(rq.getBrandMember());
        model.addAttribute("actorRestCash", actorRestCash);

        return "trendpick/admin/withDraw";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/withDraw")
    public String apply(@Valid WithDrawApplyForm withDrawApplyForm) {
        RsData<WithdrawApply> rsData = withdrawService.apply(
                withDrawApplyForm.getBankName(),
                withDrawApplyForm.getBankAccountNo(),
                withDrawApplyForm.getPrice(),
                rq.getBrandMember()
        );

        return rq.redirectWithMsg("/trendpick/admin/withDrawList","출금 신청이 완료되었습니다.");
    }
}
