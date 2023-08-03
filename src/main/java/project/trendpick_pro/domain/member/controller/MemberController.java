package project.trendpick_pro.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.trendpick_pro.domain.member.entity.dto.response.MemberInfoResponse;
import project.trendpick_pro.domain.member.entity.form.AddressForm;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.member.service.MemberService;
import project.trendpick_pro.global.basedata.tagname.service.TagNameService;
import project.trendpick_pro.global.util.rq.Rq;
import project.trendpick_pro.global.util.rsData.RsData;

@RequestMapping("/trendpick/member")
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final Rq rq;

    private final MemberService memberService;
    private final TagNameService tagNameService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String join(JoinForm joinForm, Model model) {
        model.addAttribute("joinForm", joinForm);
        model.addAttribute("allTags", tagNameService.findAll());
        return "trendpick/usr/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register")
    public String join(@Valid JoinForm joinForm) {
        RsData<Long> memberId = memberService.join(joinForm);
        if (memberId.isFail())
            return rq.historyBack(memberId);
        return rq.redirectWithMsg("/trendpick/member/login", memberId);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "trendpick/usr/member/login";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/info")
    public String myInfo(Model model) {
        model.addAttribute("MemberInfo", MemberInfoResponse.of(rq.getLogin()));
        model.addAttribute("myTags", rq.getMember().getTags());
        return "trendpick/usr/member/info";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @GetMapping("/edit/address")
    public String modifyAddress(Model model) {
        model.addAttribute("originalAdrress", rq.getMember().getAddress());
        return "trendpick/usr/member/address";
    }

    @PreAuthorize("hasAuthority({'MEMBER'})")
    @PostMapping("/edit/address")
    public String modifyAddress(@Valid AddressForm addressForm, Model model) {
        RsData<MemberInfoResponse> member = memberService.modifyAddress(rq.getLogin(), addressForm.address());
        model.addAttribute("MemberInfo", member);
        return rq.redirectWithMsg("/trendpick/member/info", member);
    }
}
