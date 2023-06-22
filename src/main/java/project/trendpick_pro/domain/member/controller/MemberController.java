package project.trendpick_pro.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import project.trendpick_pro.domain.recommend.service.RecommendService;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;
import project.trendpick_pro.global.rsData.RsData;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/member")
public class MemberController {

    private final MemberService memberService;
    private final TagNameService tagNameService;
    private final RecommendService recommendService;

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

        RsData<Member> member = memberService.register(joinForm);
        if (member.isFail())
            return rq.historyBack(member);

        if(member.getData().getRole().getValue().equals("MEMBER"))
            recommendService.select(member.getData().getEmail());

        return rq.redirectWithMsg("/trendpick/member/login", member);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "trendpick/usr/member/login";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/info")
    public String showMe(Model model) {
        Member member = rq.getLogin();
        model.addAttribute("MemberInfo", MemberInfoDto.of(member));
        return "trendpick/usr/member/info";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/info/address")
    public String registerAddress(AddressForm addressForm, Model model) {
        model.addAttribute("addressForm", addressForm);
        return "trendpick/usr/member/address";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/info/address")
    public String manageAddress(@ModelAttribute @Valid AddressForm addressForm, Model model) {

        RsData<Member> member = memberService.manageAddress(rq.getLogin(), addressForm.address());
        model.addAttribute("MemberInfo", MemberInfoDto.of(member.getData()));

        return rq.redirectWithMsg("/trendpick/member/info", member);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/edit/address")
    public String editAddress(AddressForm addressForm, Model model) {
        model.addAttribute("originalAdrress", rq.getMember().getAddress());
        model.addAttribute("addressForm", addressForm);
        return "trendpick/usr/member/address";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/edit/address")
    public String updateAddress(@ModelAttribute @Valid AddressForm addressForm, Model model) {

        RsData<Member> member = memberService.manageAddress(rq.getLogin(), addressForm.address());
        model.addAttribute("MemberInfo", MemberInfoDto.of(member.getData()));

        return rq.redirectWithMsg("/trendpick/member/info", member);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/info/account")
    public String registerAccount(AccountForm accountForm, Model model){
        model.addAttribute("accountForm", accountForm);
        return "trendpick/usr/member/account";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/info/account")
    public String manageAccount(@ModelAttribute @Valid AccountForm accountForm, Model model) {
        RsData<Member> member = memberService.manageAccount(rq.getLogin(), accountForm.bankName(), accountForm.bankAccount());
        model.addAttribute("MemberInfo", MemberInfoDto.of(member.getData()));

        return rq.redirectWithMsg("/trendpick/member/info", member);
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/edit/account")
    public String editAccount(AccountForm accountForm, Model model){
        model.addAttribute("accountForm", accountForm);
        return "trendpick/usr/member/account";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/edit/account")
    public String updateAccount(@ModelAttribute @Valid AccountForm accountForm, Model model) {
        RsData<Member> member = memberService.manageAccount(rq.getLogin(), accountForm.bankName(), accountForm.bankAccount());
        model.addAttribute("MemberInfo", MemberInfoDto.of(member.getData()));

        return rq.redirectWithMsg("/trendpick/member/info", member);
    }
}
