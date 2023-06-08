package project.trendpick_pro.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.member.entity.dto.MemberInfoDto;
import project.trendpick_pro.domain.member.entity.form.AccountForm;
import project.trendpick_pro.domain.member.entity.form.AddressForm;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.domain.tags.tag.service.TagService;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/member")
public class MemberController {

    private final MemberService memberService;
    private final TagNameService tagNameService;

    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String register(JoinForm joinForm, Model model) {
        model.addAttribute("joinForm", joinForm);
        model.addAttribute("allTags", tagNameService.findAll());
        return "trendpick/usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public String register(@ModelAttribute @Valid JoinForm joinForm) {
        memberService.register(joinForm);
        return "redirect:/trendpick/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "trendpick/usr/member/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String showMe(Model model) {
        Optional<Member> member = rq.CheckLogin();
        Member actor = member.get();
        model.addAttribute("MemberInfo", MemberInfoDto.of(actor));
        return "trendpick/usr/member/info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/address")
    public String registerAddress(AddressForm addressForm, Model model) {
        model.addAttribute("addressForm", addressForm);
        return "trendpick/usr/member/address";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/address")
    public String manageAddress(@ModelAttribute @Valid AddressForm addressForm, Model model) {
        Optional<Member> member = rq.CheckLogin();
        Member actor = member.get();
        memberService.manageAddress(actor, addressForm.address());
        model.addAttribute("MemberInfo", MemberInfoDto.of(actor));


        return "redirect:/trendpick/member/info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/account")
    public String registerAccount(AccountForm accountForm, Model model){
        model.addAttribute("accountForm", accountForm);
        return "trendpick/usr/member/account";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/account")
    public String manageAccount(@ModelAttribute @Valid AccountForm accountForm, Model model) {
        Optional<Member> member = rq.CheckLogin();
        Member actor = member.get();
        memberService.manageAccount(actor, accountForm.bankName(), accountForm.bankAccount());
        model.addAttribute("MemberInfo", MemberInfoDto.of(actor));

        return "redirect:/trendpick/member/info";
    }
}
