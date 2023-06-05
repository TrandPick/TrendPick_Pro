package project.trendpick_pro.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.tags.tag.service.TagService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/member")
public class MemberController {

    private final MemberService memberService;
    private final TagService tagService;

    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String register(JoinForm joinForm, Model model) {
        model.addAttribute("joinForm", joinForm);
        model.addAttribute("allTags", tagService.getAllTags());
        return "trendpick/usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public String register(@Valid JoinForm joinForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", tagService.getAllTags());
            return "trendpick/usr/member/join";
        }
        memberService.register(joinForm);
        return "redirect:/trendpick/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "/trendpick/usr/member/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String showMe() {
        return "trendpick/usr/member/info";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/address-edit")
    public String manageAddress(String address) {
        Member actor = rq.getMember();
        memberService.manageAddress(actor, address);

        return "trendpick/usr/member/info";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/account")
    public String manageAccount(String bank_name, String account) {
        Member actor = rq.getMember();
        memberService.manageAccount(actor, bank_name, account);

        return "trendpick/usr/member/info";
    }
}
